rootProject.name = "backend"

include("user-service")
include("products-service")
include("inventory-service")

project(":user-service").projectDir = file("user-service")
project(":products-service").projectDir = file("products-service")
project(":inventory-service").projectDir = file("inventory-service")

