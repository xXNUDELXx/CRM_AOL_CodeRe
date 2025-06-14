package pl.coderslab.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;
import pl.coderslab.entity.Event;
import pl.coderslab.entity.Notification;
import pl.coderslab.repository.EventRepository;
import pl.coderslab.repository.NotificationRepository;

@Service
@Log4j
public class NotificationService {

	private EventRepository eventRepository;
	private NotificationRepository notificationRepository;

	private List<Event> todayEventList = new ArrayList<>();

	public NotificationService(EventRepository eventRepository, NotificationRepository notificationRepository) {
		super();
		this.eventRepository = eventRepository;
		this.notificationRepository = notificationRepository;
	}


	/**Checks if any notifications schould by generated 
	 * any 5 minutes 9-17 Monday to Friday 
	 * and generates them
	 *
	 */
	@Scheduled(cron = "0 */5 9-17 * * MON-FRI")
public void checkIfGenerateNotification() {
    for (Iterator<Event> iterator = todayEventList.iterator(); iterator.hasNext(); ) {
        Event event = iterator.next();

        if (isEventInPast(event)) {
            deleteNotificationsForEvent(event);
            iterator.remove();
        } else if (shouldNotifyOneHourBefore(event)) {
            generateNotification(event);
        } else if (shouldNotifyTwoHoursBefore(event)) {
            generateNotification(event);
        }
    }
}

private boolean isEventInPast(Event event) {
    return LocalDateTime.now().isAfter(event.getTime());
}

private void deleteNotificationsForEvent(Event event) {
    List<Notification> list = notificationRepository.findByEvent(event);
    for (Notification n : list) {
        notificationRepository.delete(n);
    }
}

private boolean shouldNotifyOneHourBefore(Event event) {
    List<Notification> notificationList = notificationRepository.findByEvent(event);
    return LocalDateTime.now().plusHours(1).isAfter(event.getTime()) && notificationList.size() < 3;
}

private boolean shouldNotifyTwoHoursBefore(Event event) {
    List<Notification> notificationList = notificationRepository.findByEvent(event);
    return LocalDateTime.now().plusHours(2).isAfter(event.getTime()) && notificationList.size() < 2;
}

	
	/**Gets list of today's events at 8.30 from Monday to Friday 
	 * and generates first notifications
	 * 
	 */
	@Scheduled(cron = "0 30 8 * * MON-FRI")
	public void generateMorningNotifications() {
		todayEventList = eventRepository.findByTimeBetween(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
		// generate notifications for all today's events
		for (Event event : todayEventList) {
			this.generateNotification(event);
		}
	}

	/**Generates and saves to db new Notification for given Event
	 * 
	 * @param event
	 */
	private void generateNotification(Event event) {
		Notification notification = new Notification();
		notification.setCreated(LocalDateTime.now());
		notification.setEvent(event);
		notification.setWasRead(false);
		notification.setUser(event.getUser());
		notification.setContent(
				"You have " + event.getType() + " with " + event.getClient().getName() + " at " + event.getTime()
				+". Topic: "+event.getTitle());
		notificationRepository.save(notification);
	}

}
