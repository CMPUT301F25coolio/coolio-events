# Object-Oriented Analysis: CRC Cards

## Welcome Screen
### Responsibilities:
- Display initial navigation options: “Log In” or “Sign Up”.
- Route users to the appropriate page based on selection.
- Maintain a visually engaging entry point (branding, theme colors).
### Collaborators: 
- LoginScreen
- SignUpScreen
- NavigationController

## Login Screen
### Responsibilities:
- Provide input fields for email and password.
- Validate inputs (non-empty, correct format).
- Call AuthController to verify credentials.
- Display errors (invalid credentials, empty fields).
- Provide “Forgot Password” and “Sign Up” redirects.
### Collaborators:
- AuthController
- Validator
- ForgotPasswordScreen
- SignUpScreen
- NotificationService

## Forgot Password Screen
### Responsibilities:
- Accept user’s registered email.
- Send password reset request to backend.
- Display confirmation when reset link is sent.
## Collaborators:
- AuthController
- EmailService
- Validator
- NotificationService

## Sign Up Screen
### Responsibilities:
- Provide fields for email and password input.
- Validate and sanitize input.
- Trigger account creation through AuthController.
- Offer “Already have an account? Log In” redirect.
### Collaborators:
- AuthController
- Validator
- NotificationService
- ChooseRoleScreen
- LoginScreen

## Choose Role Screen  
### Responsibilities:
- Display available roles (Organiser, Entrant, Administrator).  
- Send selected role to backend for user profile setup.  
- Route user to the next appropriate dashboard view.  
## Collaborators:
- RoleController  
- AuthController  
- NavigationController  
- UserProfile  

## AuthController  
### Responsibilities:
- Handle login, signup, and password reset logic.  
- Interact with backend APIs.  
- Store authentication tokens or session info.  
## Collaborators:
- Validator  
- NotificationService  
- UserRepository  
- TokenService  

## Validator  
### Responsibilities:
- Validate input fields (email format, password length, etc.).  
- Return user-friendly error messages.  
## Collaborators:
- LoginScreen  
- SignUpScreen  
- ForgotPasswordScreen  

## NotificationService  
### Responsibilities:
- Show success, warning, or error messages to user.  
- Manage toast/snackbar or dialog prompts.  
## Collaborators:
- WelcomeScreen  
- LoginScreen  
- SignUpScreen  
- ForgotPasswordScreen  
- ChooseRoleScreen  

## NavigationController  
### Responsibilities:
- Handle page transitions and routing logic between screens.  
## Collaborators:
- WelcomeScreen  
- LoginScreen  
- SignUpScreen  
- ChooseRoleScreen  

## RoleController  
### Responsibilities:
- Assign user roles (Organiser, Entrant, Administrator).  
- Update user profile and permissions accordingly.  
- Notify the backend of role selections.  
## Collaborators:
- ChooseRoleScreen  
- AuthController  
- UserProfile  

## UserProfile  
### Responsibilities:
- Store and manage user data and selected role.  
- Provide access to personalized dashboards or views.  
## Collaborators:
- RoleController  
- AuthController  

## Entrant
### Responsibilities:
- Has profile
- Maintains list of their events
- Search for events
- View event details
- Entering the wait list for an event
- Register or decline an event, or cancel registration
- Receives notifications 
- Scan QR code
### Collaborators:
- Profile
- Search
- Waitlist
- Event
- EventList
- Notification
- Camera 

## Profile 
### Responsibilities:
- Stores Name
- Stores Email
- Stores password
- Stores phone number (optional)
### Collaborators:
- Entrant
- Organizer
- Administrator

## Organizer
### Responsibilities:
- Has profile
- Maintains list of events made 
- Make events
- Generate QR code for event
- Upload poster image for events
- Edit events
- View entrants for an event
- Access the waitlist
- Send entrants notification
- Remove unregistered entrants

### Collaborators:
- Profile
- EventList
- Event
- QR code
- Waitlist 
- Notification 
- Lottery
- Camera

## Event
### Responsibilities:
- Has event details
- Created by organizers 
- Maintains a waitlist of entrants
- Maintains a chosen list of entrants
- Maintains list of cancelled entrants 
- Associated with a QR code


### Collaborators:
- EventDetails 
- Organizer
- Entrant
- Waitlist
- Notification 
- QR Code

