# Currency Rate Service

## Overview
This service fetches currency exchange rates from external source (exchangerate.host) and returns them to client

## API Endpoints
The application exposes the following endpoints:
1. **Get Exchange Rate from Currency A to Currency B**  
   Retrieves the exchange rate between two specific currencies.
2. **Get All Exchange Rates from Currency A**  
   Retrieves all available exchange rates using a specified currency as the base.
3. **Get Value Conversion from Currency A to Currency B**  
   Converts a given amount from a source currency into a target currency.
4. **Get Value Conversion from Currency A to a List of Supplied Currencies**  
   Converts a specified amount from a base currency into multiple target currencies simultaneously.

### API Documentation
Interactive API documentation is available via Swagger:  
[Swagger UI](http://localhost:8080/swagger-ui/index.html#/)

## How to Run the Application
To start the application, follow these steps:
1. Launch the application from the `CurrencyRateApplication` class.

