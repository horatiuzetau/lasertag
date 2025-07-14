# Laser Tag Reservation API

This API was designed to manage reservations of a local Laser Tag centre in Focsani. Users are able to see which slots are available for specific activities and book them.

---

## Main functionalities of the application

- **[ADMIN]** Manage activities (e.g., Laser Tag session, PlayStation 5, etc.)
- **[UNAUTHENTICATED USER]** Book time slots for the defined activities
- **[ADMIN]** Manage clients (clients' data obtained when users perform a successful reservation)
- **[ADMIN]** Manage schedule (a.k.a. time ranges of a day when the centre is open; e.g., Monday 10AM - 2PM; 4PM - 10PM)
- **[ADMIN]** Authentication (registration is commented, because the application doesn't support other roles than admin)
- **[ADMIN]** Manage settings (e.g., `bookings.enabled` specifies if reservations are allowed or not)
- **[ADMIN]** Statistics (not yet implemented)

---

## Concepts used

- **LiquiBase** used for database versioning. For the current state of the application, it was only used for database initialization.
- **Basic Spring Security implementation** for authentication and authorization: every request goes through the JWT Authentication filter, where:
    1. JWT gets extracted from the Authorization header
    2. The username gets extracted from the token
    3. User details are loaded by username, by using a custom implementation of `UserDetailsService`
    4. Token gets validated
    5. Authentication token is set within the Security Context
- **Database interaction** using Spring Data JPA and Hibernate
- **JPA Auditing** for audit columns (`createdAt` / `updatedAt`)
- **Lombok** for generating getters, setters, constructors, and field defaults
- **Docker** - the docker-compose.yml was used to deploy the API on DigitalOcean

---

## Notes

- Application is still (and will remain) in the MVP state. Exception handling was not implemented, because all the validations and exceptions are handled by the front-end. We needed a working API that can be used as soon as possible.
- For the same reason (desire of fast delivery and acceptance for corrupted data), tests were not created.

---

## Application setup

1. **Create a Docker container**
   ```bash
   docker pull postgres:14.5
   docker run --name lasertag-db -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres:14.5
   ```

2. **Edit configuration**, by adding the following values to the environment variables:
   ```
   SPRING_DATASOURCE_USERNAME=postgres;SPRING_DATASOURCE_PASSWORD=pass;SPRING_CUSTOM_JWT_KEY=testkey
   ```

3. **Run the application.** By doing so, Liquibase will run and:
    - Initialize all the database tables
    - Create roles (in this case `ADMIN`)
    - Create admin user (`username: admin`)
    - Create relevant settings (`bookings.enabled / true`)

---

## To authenticate

1. Import the Postman collection
2. Go to **Auth > Login** and perform the request
3. Copy the token from the response
4. Go to **Collection (Laser Tag API) > Authorization**
5. In the **Auth Type** dropdown, select **Bearer Token**
6. Paste the generated token

➡️ You are now authorized to perform protected requests

---

## Flows

> Asterix up-front = Required to run at least once because the data created by them is used within other flows*

1. ***[AUTH REQUIRED] Create schedule**
2. ***[AUTH REQUIRED] Create activity**  
   Creates a shareable activity (Laser Tag)  
   *(shareable = multiple different reservations can be performed by different people, until the maximum "capacity" is met)*
3. **Get availability map**  
   Returns the available time slots of a given date for each activity. It contains the capacity, booked spots, available spots, and a boolean indicating if the slot is fully booked or not.  
   *(I know the data can be computed on the front end, but I've implemented like this to make my life simpler and readable in the front-end implementation.)*
4. **Book Slot**
    1. Get availability map for a specific day, to see what time slots are available, partially booked, or fully booked
    2. Create Slot:
        - `activityId` is `1` (the only activity created in previous flow)
        - `date` - date used for availability map
        - `startTime` - time selected from the availability map
    3. Get availability map again, to verify that the slot availability has changed
5. **[AUTH REQUIRED] Cancel Booking**
    1. Get all slots and copy the ID of the slot you want to cancel (`status` is `BOOKED`)
    2. Patch Slot, using the previously copied ID
    3. Get all slots again, to verify that the slot was cancelled *(it's not showing anymore in the response, because the status got updated to `CANCELLED`)*