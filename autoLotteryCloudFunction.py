
from firebase_functions import scheduler_fn, firestore_fn, https_fn
from google.api_core.datetime_helpers import DatetimeWithNanoseconds
import datetime
import random
import uuid
import firebase_admin
from firebase_admin import initialize_app, firestore
import google.cloud.firestore

firebase_admin.initialize_app()

def createNotifications(eventID, eventName, organizerID, notchosenEntrants, chosenEntrants):
    #Creates notifications for an event's lottery system
    db = firestore.client()
    notifs = db.collection("notifications")
    print("Creating Notifications...")
    
    for entrant in chosenEntrants:
        #Create notifications for chosen entrants
        notifId = str(uuid.uuid4())
        entrantUsername = getUsername(entrant)
        notifs.document(notifId).set({
            "createdAt":DatetimeWithNanoseconds.now(),
            "notifId":notifId,
            "eventId":eventID,
            "title":f'You have been chosen for "{eventName}"',
            "message":f'You have been chosen for "{eventName}", Register for the event under my events!',
            "type":"entrantChosen",
            "uid":entrant,
            "shown": False,
            "sender": "System",
            "receiver":entrantUsername
        })
    

    for entrant in notchosenEntrants:
        #Create notifications for not chosen entrants
        notifId = str(uuid.uuid4())
        entrantUsername = getUsername(entrant)
        notifs.document(notifId).set({
            "createdAt":DatetimeWithNanoseconds.now(),
            "notifId":notifId,
            "eventId":eventID,
            "title":f'You have not been chosen for"{eventName}"',
            "message":f'You have not been chosen for "{eventName}"',
            "type":"entrantNotChosen",
            "uid":entrant,
            "shown": False,
            "sender": "System",
            "receiver":entrantUsername
        })

    #Create notification for organizer
    notifId = str(uuid.uuid4())
    orgUsername = getUsername(organizerID)
    notifs.document(notifId).set({
        "createdAt":DatetimeWithNanoseconds.now(),
        "notifId":notifId,
        "eventId":eventID,
        "title":f'Lottery Drawn for "{eventName}"',
        "message":f"Your event: {eventName}'s lottery has been drawn, View the results under my events.",
        "type":"organizerLotteryDone",
        "uid":organizerID,
        "shown": False,
        "sender": "System",
        "receiver":orgUsername
    })

def getUsername(userId):
    db = firestore.client()
    doc_ref = db.collection("users").document(userId)
    try:
        doc = doc_ref.get()
        if doc.exists:
            return doc.to_dict()["username"] #Return username
        else:
            return "UNKNOWN" #Return unknown
    except Exception as e:
        print("Error getting user")

#Run daily
@scheduler_fn.on_schedule(schedule="0 7 * * *")
def autoLottery(cur_event: scheduler_fn.ScheduledEvent):
    db = firestore.client()
    docs = db.collection("events").stream()
    print("Running daily auto lottery on events...")
    for doc in docs:
        event = doc.to_dict()
        eventDetails = event["details"]
        now =  DatetimeWithNanoseconds.now()
        if (event["lotteryDone"] == False):
            # If lottery not drawn yet we should draw the lottery
            if  (now >= eventDetails["endDate"].replace(tzinfo=None)):
                # If current time is greater then endDate then we should draw
                entrantsToChoose = event["waitlistEntrants"]
                selectedEntrants = event["chosenEntrants"]
                entrantLimit = eventDetails["entrantLimit"]
                if (len(event["waitlistEntrants"]) == 0):
                    continue
                
                if (len(entrantsToChoose) <= entrantLimit - len(selectedEntrants)):
                    selectedEntrants += entrantsToChoose
                    entrantsToChoose.clear()

                else:
                    for i in range(entrantLimit):
                        randomIndex = random.randint(0,len(entrantsToChoose) - 1)
                        randomEntrant = entrantsToChoose[randomIndex]

                        selectedEntrants.append(randomEntrant)
                        entrantsToChoose.remove(randomEntrant)
                
                print(f'{eventDetails["eventName"]}({doc.id}): waitListEntrants: {entrantsToChoose} selectedEntrants:{selectedEntrants}')
                db.collection("events").document(doc.id).update({
                    "lotteryDone":True,
                    "waitlistEntrants":entrantsToChoose,
                    "chosenEntrants":selectedEntrants
                })
                createNotifications(doc.id, eventDetails["eventName"], event["organizerId"], entrantsToChoose, selectedEntrants)

    return




#
    

        


@https_fn.on_request()
def testAutoLottery(req: https_fn.Request):
    db = firestore.client()
    docs = db.collection("events").stream()
    
    for doc in docs:
        event = doc.to_dict()
        eventDetails = event["details"]
        now =  DatetimeWithNanoseconds.now()
        if (event["lotteryDone"] == False):
            # If lottery not drawn yet we should draw the lottery
            if  (now >= eventDetails["endDate"].replace(tzinfo=None)):
                # If current time is greater then endDate then we should draw
                entrantsToChoose = event["waitlistEntrants"]
                selectedEntrants = event["chosenEntrants"]
                entrantLimit = eventDetails["entrantLimit"]
                if (len(event["waitlistEntrants"]) == 0):
                    continue
                
                if (len(entrantsToChoose) <= entrantLimit - len(selectedEntrants)):
                    selectedEntrants += entrantsToChoose
                    entrantsToChoose.clear()

                else:
                    for i in range(entrantLimit):
                        randomIndex = random.randint(0,len(entrantsToChoose) - 1)
                        randomEntrant = entrantsToChoose[randomIndex]

                        selectedEntrants.append(randomEntrant)
                        entrantsToChoose.remove(randomEntrant)
                
                print(f'{eventDetails["eventName"]}({doc.id}): waitListEntrants: {entrantsToChoose} selectedEntrants:{selectedEntrants}')
                db.collection("events").document(doc.id).update({
                    "lotteryDone":True,
                    "waitlistEntrants":entrantsToChoose,
                    "chosenEntrants":selectedEntrants
                })
                createNotifications(doc.id, eventDetails["eventName"], event["organizerId"], entrantsToChoose, selectedEntrants)

    return

                



        
       
