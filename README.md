### Item Sharing Service (java-shareit)

This service allows users to rent items, make reservations, leave reviews, and submit requests for adding items. 
The backend for this web application is developed in Java using SpringBoot.

- **Technology Stack**: ```Java 11```, ```SpringBoot```, ```Docker```, ```Hibernate```, ```PostgreSQL```, ```H2```, ```JUnit```, ```Mockito```, ```Lombok```.
- **Architecture**: The application follows a microservices architecture consisting of two modules, ```gateway``` and ```server```.
- **Databases**: In the default scenario, ```PostgreSQL``` is used, while ```H2``` is used for testing.
___

#### Module ```gateway```
This module is used for validation and handling of incorrect user requests. 
After validation, requests are sent to the ```server``` module, which contains the core business logic, including database operations. 
Requests are sent to the ```server``` module via ```REST```.
___

#### Core Functionality

Validated requests are directed to the main ```server``` module, which is connected to the ```PostgreSQL``` database. 
```Hibernate``` and ```JPA``` are used for database operations.

Key classes marked with ```@Entity``` and their functionality:
```Item```
- Create / update / delete items
- View a list of all owned items with names and descriptions
- Search for items by potential renters

```User```
- Create / update / delete users

```Booking```
- Add a new booking request
- Confirm or decline booking requests
- Retrieve information about a specific booking (including its status)
- Get a list of all bookings for the current user
- Get a list of bookings for all items of the current user

```Request```
- Create requests
- Retrieve a list of all requests
- Retrieve information about a specific request
___

#### Test Coverage 
More than **95%** of the code in the application is covered by testing. 
Both unit and integration tests were used:
- ```JUnit``` and ```Mockito``` for unit testing
- ```WebMvc``` and ```DataJpaTest``` for integration testing of the web layer and repository
___

#### System Requirements
- Java Development Kit (JDK) 11
- Docker
- PostgreSQL

The application can be launched through the configured docker-compose.yml file.
