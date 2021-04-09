package collection;


import data.Ticket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        Comparator<Ticket> comp = new TicketPriceComparator();
        myTreeSet = new TreeSet<>(comp);
        date = new Date();
    }

    /**
     * Увелечние номера события для билетов, для автоматической генерации id у поля Event
     */

    /**
     * Добавление элемента в коллекцию
     */
    public void add(Ticket ticket) {
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

    public boolean findId(int id) {
        return myTreeSet.stream().map(Ticket::getId).filter((w) -> w == id).count() == 1;
    }

    public boolean remove(int id) {
        Ticket ticket = null;
        int size = size();
        myTreeSet.removeIf(myTreeSet -> myTreeSet.getId() == id);
        if (size - size() == 0) {
            return false;
        }
        return true;
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

        return myTreeSet.stream().max(Ticket::compareTo).get().compareTo(ticket) < 0;

    }


    /**
     * УДаление всех элементов коллекции больших заднного
     */
    public void removeGreater(Ticket ticket) {

        //myTreeSet.removeAll(myTreeSet.tailSet(ticket, true));
        myTreeSet.removeIf(ticket1 -> ticket1.compareTo(ticket) > 0);
    }


    /**
     * Проверка является ли билет минимальным в коллекции
     */
    public boolean isMin(Ticket ticket) {
        if (myTreeSet.isEmpty())
            return true;
        return myTreeSet.stream().max(Ticket::compareTo).get().compareTo(ticket) > 0;
    }


//    public void headSet(Ticket ticket, boolean incl) {
//        myTreeSet = myTreeSet.headSet(ticket, incl);
//    }

    /**
     * Нахождение суммы полей discount
     */
    public long sumDiscount() {
        return myTreeSet.stream().mapToLong(Ticket::getDiscount).sum();
    }

    /**
     * Нахождение билета с максимальным комментарием
     */
    public Ticket maxComment() {
        return myTreeSet.stream().max(Comparator.comparing(Ticket::getComment)).orElse(null);
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
        return myTreeSet.stream().map(Ticket::getPrice).collect(Collectors.toSet());
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

    public void addAll(Set<Ticket> tickets) {
        myTreeSet.addAll(tickets);
    }

}
