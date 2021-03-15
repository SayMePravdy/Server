package data;

import java.io.Serializable;

public class Event implements Serializable {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private int minAge;
    private Integer ticketsCount; //Поле может быть null, Значение поля должно быть больше 0

    public Event(Integer id, String name, int minAge, Integer ticketsCount) {
        this.id = id;
        this.name = name;
        this.minAge = minAge;
        this.ticketsCount = ticketsCount;
    }

    @Override
    public String toString() {
        return "data.Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", minAge=" + minAge +
                ", ticketsCount=" + ticketsCount +
                '}';
    }

    public String toCsv() {
        return name + "," + minAge + "," + ticketsCount;
    }
}
