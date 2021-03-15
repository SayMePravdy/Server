package collection;


import data.Ticket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Класс коллекции, с которой мы работаем
 */

public class MyTreeSet {
    /**
     * Наша заданная колекция
     */
    private NavigableSet<Ticket> myTreeSet;
    private Date date = null;

    /**
     * Конструктор, в котором указываем компораторы
     */
    public MyTreeSet() {
        Comparator<Ticket> comp = new TicketPriceComparator().thenComparing(new TicketIdComparator());
        myTreeSet = new TreeSet<>(comp);
    }

    /**
     * Увелечние номера события для билетов, для автоматической генерации id у поля Event
     */

    /**
     * Добавление элемента в коллекцию
     */
    public void add(Ticket ticket) {
        if (date == null) {
            date = new Date();
        }
        myTreeSet.add(ticket);
    }

    /**
     * Удаление элемента коллекции
     */

    public void remove(Ticket ticket) {
        myTreeSet.remove(ticket);
    }

    /**
     * Удаление элемента коллекции по его id
     */

    public boolean remove(int id) {
        Ticket ticket = null;
        for (Ticket t : myTreeSet) {
            if (t.getId() == id) {
                ticket = t;
            }
        }
        if (ticket == null) {
            return false;
        } else {
            myTreeSet.remove(ticket);
            return true;
        }
    }

    /**
     * Очистка коллекции
     */
    public void clear() {
        myTreeSet.clear();
    }

    /**
     * Проверка является ли билет максимальным в коллекции
     */
    public boolean isMax(Ticket ticket) {
        if (myTreeSet.isEmpty())
            return true;

        if (ticket.compareTo(myTreeSet.last()) > 0) {
            return true;
        }
        return false;
    }


    /**
     * УДаление всех элементов коллекции больших заднного
     */
    public void removeGreater(Ticket ticket) {
        myTreeSet.removeAll(myTreeSet.tailSet(ticket, true));
    }


    /**
     * Проверка является ли билет минимальным в коллекции
     */
    public boolean isMin(Ticket ticket) {
        if (myTreeSet.isEmpty())
            return true;

        if (ticket.compareTo(myTreeSet.first()) < 0) {
            return true;
        }
        return false;
    }


//    public void headSet(Ticket ticket, boolean incl) {
//        myTreeSet = myTreeSet.headSet(ticket, incl);
//    }

    /**
     * Нахождение суммы полей discount
     */
    public int sumDiscount() {
        int sum = 0;
        for (Ticket t : myTreeSet) {
            sum += t.getDiscount();
        }
        return sum;
    }

    /**
     * Нахождение билета с максимальным комментарием
     */
    public Ticket maxComment() {
        String max = "";
        Ticket ticket = null;
        for (Ticket t : myTreeSet) {
            if (t.getComment().compareTo(max) > 0) {
                max = t.getComment();
                ticket = t;
            }
        }
        return ticket;
    }

    /**
     * Вывод в консоль всех элемнтов коллекции
     */
    public String print() {
        StringBuilder data = new StringBuilder();
        if (myTreeSet.size() == 0) {
            data.append("Collection is Empty");
        } else {
            for (Ticket ticket : myTreeSet) {
                data.append(ticket.toString() + "\n");
            }
        }
        return data.toString();
    }

    /**
     * Сохранение коллекции в файл
     */
    public void save(FileWriter fileWriter) throws NullPointerException {
        try {
            for (Ticket ticket : myTreeSet) {
                fileWriter.write(ticket.toCsv() + "\n");
            }
        } catch (IOException e) {
        }
    }


    /**
     * Нахождение уникальных полей price
     */
    public Set<Float> uniquePrices() {
        float prevPrice = 0f;
        float price = 0f;
        float ppPrice = 0f;
        Set<Float> uniquePrices = new TreeSet<>();
        if (myTreeSet.size() == 1) {
            uniquePrices.add(myTreeSet.first().getPrice());
            return uniquePrices;
        }
        boolean first = true, second = true;
        for (Ticket t : myTreeSet) {
            if (first) {
                first = false;
                price = t.getPrice();
            } else {
                if (second) {
                    second = false;
                    prevPrice = price;
                    price = t.getPrice();
                    if (prevPrice != price) {
                        uniquePrices.add(prevPrice);
                    }
                } else {
                    ppPrice = prevPrice;
                    prevPrice = price;
                    price = t.getPrice();
                    if (prevPrice != ppPrice && prevPrice != price) {
                        uniquePrices.add(prevPrice);
                    }
                }
            }
        }
        if (price != prevPrice) {
            uniquePrices.add(price);
        }
        return uniquePrices;
    }

    /**
     * Выод информации о коллекции
     */
    public String showInfo() {
        return "Type: Ticket\n" + date + "\n" + myTreeSet.size();
    }


    /**
     * Нахождение размера коллекции
     */
    public int size() {
        return myTreeSet.size();
    }


}
