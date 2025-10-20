# Object-Oriented Analysis: CRC Cards

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

