package collection;

import data.Ticket;

import java.util.Comparator;

/**
 * Компоратор по price билета
 */

public class TicketPriceComparator implements Comparator<Ticket> {
    @Override
    public int compare(Ticket o1, Ticket o2) {
        if (o1.getPrice() - o2.getPrice() > 0)
            return 1;
        if (o1.getPrice() - o2.getPrice() < 0)
            return -1;
        return 0;
    }

}
