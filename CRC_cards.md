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

## Event Details  
### Responsibilities:
- Know Event Name  
- Know Event Description  
- Know Event Organizer  
- Know Registration Period  
- Know Status (open/closed)  
- Has event poster (optional)  
## Collaborators:
- Event  

## EventList  
### Responsibilities:
- Has list of events  
## Collaborators:
- Event  
- Organizer  
- Entrant  
- Search  
- Home
#### Note: has subclasses of EventLists: OrganizerEventList, EntrantEventList, HomeEventList, SearchEventList
- OrganizerEventList: List of events the organizer made
- EntrantEventList: List of events the entrant joined the waitlist for
- HomeEventList: List of all events that appear on the entrant’s home page
- SearchEventList: List of events shown on the search page

## OrganizerEventList  
### Responsibilities:
- List of events the organizer made  
## Collaborators:
- Event  
- Organizer  

## EntrantEventList  
### Responsibilities:
- List of events the entrant joined the waitlist for  
## Collaborators:
- Event  
- Entrant  

## HomeEventList  
### Responsibilities:
- List of all events that appear on the entrant’s home page  
## Collaborators:
- Event  
- Entrant  

## SearchEventList  
### Responsibilities:
- List of events shown on the search page  
## Collaborators:
- Event  
- Entrant  

## Search  
### Responsibilities:
- Search events in an event list by the keyword entrant provided  
- Return filtered results from event lists  
## Collaborators:
- EventList  
- Entrant  

## WaitList  
### Responsibilities:
- Store list of entrants for each event  
- Sends list to lottery for entrants to be chosen  
## Collaborators:
- Event  
- Entrant  
- Organizer  
- Lottery  

## Lottery  
### Responsibilities:
- Randomly draws a specified number of entrants  
- Updates the chosen list of entrants for an Event  
- Notify entrants who were selected or not  
## Collaborators:
- WaitList  
- Event  
- Notification  

## Administrator  
### Responsibilities:
- Has profile  
- Manage organizers who violate app policy  
- Browse events, profiles and logs  
- Monitor participant activity (entrants, organizers and events)  
- Review notification logs sent to Entrants  
- Remove events or users  
## Collaborators:
- Organizer  
- Entrant  
- Event  

## QR Code  
### Responsibilities:
- Generate QR code for the event  
- Connect QR code to an event  
- Scanned by camera  
## Collaborators:
- Organizer  
- Event  
- Camera  

## Camera  
### Responsibilities:
- Scan the QR code  
- Take picture for posters  
## Collaborators:
- Event  
- QR Code  

## Notification  
### Responsibilities:
- Store Entrants who received the notification  
- Store Organizer who sent the notification  
- Will have subclasses based on the different notification type:  
  - LotteryNotification (for selected entrants)  
  - WaitlistNotification (for entrants on waitlist)  
  - CancellationNotification (for cancelled entrants)  
## Collaborators:
- Organizer  
- Entrant  
- Event  
- Administrator  



