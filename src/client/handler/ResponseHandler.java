package client.handler;

import shared.model.Person;
import shared.network.Response;

import java.util.List;

public class ResponseHandler {

    public void handle(Response response) {
        if (!response.isSuccess()) {
            System.out.println("Ошибка: " + response.getMessage());
            return;
        }

        if (response.getCollection() != null) {
            printCollection(response.getCollection());
        } else if (response.getInfo() != null && !response.getInfo().isEmpty()) {
            System.out.println(response.getInfo());
        } else if (response.getMessage() != null && !response.getMessage().isEmpty()) {
            System.out.println(response.getMessage());
        }

        if (response.getData() != null) {
            printData(response.getData());
        }
    }

    private void printCollection(List<Person> collection) {
        if (collection.isEmpty()) {
            System.out.println("Коллекция пуста");
            return;
        }

        System.out.println("*Элементы коллекции*");
        for (Person person : collection) {
            System.out.println(person);
        }
        System.out.println("Всего элементов: " + collection.size());
    }

    private void printData(Object data) {
        if (data instanceof List<?> list) {
            if (list.isEmpty()) {
                System.out.println("Нет данных");
                return;
            }
            for (Object item : list) {
                System.out.println(item);
            }
        } else {
            System.out.println(data);
        }
    }
}