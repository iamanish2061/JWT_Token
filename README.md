Hi! I have tried to implement JWT Token using spring boot and spring security

Flow of the activities and description
1. User registers the account
    i. the request is handled by endpoint: /register
    ii. register method of UserService is called
    iii. the method checks if username is unique, email is unique 
    iv. save and return the user if valid else throw exception

2. Logs into the account
   i. the request is handled by endpoint: /login
   ii. login method of UserService is called
   iii. spring security does its internal function which is explained in the method login
   iv. if authentication is successfully done, tokens are generated 
        -> access token , which is for short period of time (30 min)
        -> refresh token, for 7 days (used to refresh the access token , if access token is expired)
    v. refresh token is stored in db and both tokens are returned to the user

3. while refreshing 
    i. the request is handled by endpoint: /refresh
    ii. refresh method of UserService is called
    iii. refresh token acts as the access token in JwtFilter (the request needs to be sent like that from frontend)
    iv. find the user by refresh token from db, and new access token and refresh token are generated
    v. updates the refresh token in database and return both tokens to the user

4. Users class is used as the entity, that holds info about user

5. UserPrincipal class implements UserDetails interface which is used by Spring Security for major actions and functions

6. CustomUserDetailsService class implements UserDetailsService, used to implement method loadByUsername
   this method returns the object of UserPrincipal class which implements UserDetails interface by wrapping the object of Users class

7. SecurityConfig is the configuration class which has the security filter

8. JwtFilter is the class (filter) that runs before every request just before security filter

9. JwtService class has the methods needed to create token, extract username, validate token and so on

Detailed Flow of an application
1. User registers
2. User logs in
3. The spring authentication is done and after validating it returns token
4. in the next request, security context is null.
    JwtFilter intercepts the request, validates the token and forwards to security filter
    Security filter does role based authentication and forwards to the actual endpoint
5. The end point handles respectively
6. In each request , the flow is same
7. if the token is expired, the server sends 401 code
    then user needs to refresh, which generates the new access and refresh token