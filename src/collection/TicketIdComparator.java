package collection;


import data.Ticket;

import java.util.Comparator;

/**
 * Компоратор по id билета
 */

public class TicketIdComparator implements Comparator<Ticket> {

    @Override
    public int compare(Ticket o1, Ticket o2) {
        return o1.getId() - o2.getId();
    }
}
