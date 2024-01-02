terraform {
  required_version = ">= 1.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">=3.3.0"
    }
  }
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "spring-group" {
  location = "eastus"
  name     = "spring-apps"
}

resource "azurerm_cosmosdb_account" "sample-db-account" {
  name                = "sample-pminkows-cosmosdb"
  location            = azurerm_resource_group.spring-group.location
  resource_group_name = azurerm_resource_group.spring-group.name
  offer_type          = "Standard"

  consistency_policy {
    consistency_level = "Session"
  }

  geo_location {
    failover_priority = 0
    location          = "eastus"
  }
}

resource "azurerm_cosmosdb_sql_database" "sample-db" {
  name                = "sampledb"
  resource_group_name = azurerm_cosmosdb_account.sample-db-account.resource_group_name
  account_name        = azurerm_cosmosdb_account.sample-db-account.name
}

resource "azurerm_cosmosdb_sql_container" "sample-db-container" {
  name                  = "accounts"
  resource_group_name   = azurerm_cosmosdb_account.sample-db-account.resource_group_name
  account_name          = azurerm_cosmosdb_account.sample-db-account.name
  database_name         = azurerm_cosmosdb_sql_database.sample-db.name
  partition_key_path    = "/customerId"
  partition_key_version = 1
  throughput            = 400
}

data "azurerm_client_config" "data" {}

resource "azurerm_role_assignment" "app_configuration_role" {
  scope                = azurerm_resource_group.spring-group.id
  role_definition_name = "App Configuration Data Owner"
  principal_id         = data.azurerm_client_config.data.object_id
}

resource "time_sleep" "role_assignment_sleep" {
  create_duration = "60s"

  triggers = {
    role_assignment = azurerm_role_assignment.app_configuration_role.id
  }
}

resource "azurerm_app_configuration" "sample-config" {
  name                = "sample-spring-cloud-config"
  resource_group_name = azurerm_resource_group.spring-group.name
  location            = azurerm_resource_group.spring-group.location

  depends_on = [azurerm_role_assignment.app_configuration_role, time_sleep.role_assignment_sleep]
}

resource "azurerm_app_configuration_key" "cosmosdb-key" {
  configuration_store_id = azurerm_app_configuration.sample-config.id
  key                    = "/application/spring.cloud.azure.cosmos.key"
  value                  = azurerm_cosmosdb_account.sample-db-account.primary_key
}

resource "azurerm_app_configuration_key" "cosmosdb-database" {
  configuration_store_id = azurerm_app_configuration.sample-config.id
  key                    = "/application/spring.cloud.azure.cosmos.database"
  value                  = "sampledb"
}

resource "azurerm_app_configuration_key" "cosmosdb-endpoint" {
  configuration_store_id = azurerm_app_configuration.sample-config.id
  key                    = "/application/spring.cloud.azure.cosmos.endpoint"
  value                  = azurerm_cosmosdb_account.sample-db-account.endpoint
}

resource "azurerm_application_insights" "spring-insights" {
  name                = "spring-insights"
  location            = azurerm_resource_group.spring-group.location
  resource_group_name = azurerm_resource_group.spring-group.name
  application_type    = "web"
}

resource "azurerm_spring_cloud_service" "spring-cloud-apps" {
  name                = "sample-spring-cloud-apps"
  location            = azurerm_resource_group.spring-group.location
  resource_group_name = azurerm_resource_group.spring-group.name
  sku_name            = "S0"

  trace {
    connection_string = azurerm_application_insights.spring-insights.connection_string
    sample_rate       = 10.0
  }

  tags = {
    Env = "Staging"
  }
}

resource "azurerm_spring_cloud_app" "account-service" {
  name                = "account-service"
  resource_group_name = azurerm_resource_group.spring-group.name
  service_name        = azurerm_spring_cloud_service.spring-cloud-apps.name

  identity {
    type = "SystemAssigned"
  }
}

resource "azurerm_spring_cloud_java_deployment" "slot-staging" {
  name                = "dep1"
  spring_cloud_app_id = azurerm_spring_cloud_app.account-service.id
  instance_count      = 1
  jvm_options         = "-XX:+PrintGC"
  runtime_version     = "Java_17"

  quota {
    cpu    = "500m"
    memory = "1Gi"
  }

  environment_variables = {
    "Env" : "Staging",
    "APP_CONFIGURATION_CONNECTION_STRING": azurerm_app_configuration.sample-config.endpoint
  }
}

resource "azurerm_spring_cloud_active_deployment" "dep-staging" {
  spring_cloud_app_id = azurerm_spring_cloud_app.account-service.id
  deployment_name     = azurerm_spring_cloud_java_deployment.slot-staging.name
}