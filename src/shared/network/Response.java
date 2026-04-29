package shared.network;

import shared.model.Person;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final List<Person> collection;
    private final String info;
    private final Object data;

    private Response(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.collection = builder.collection;
        this.info = builder.info;
        this.data = builder.data;
    }

    public static class Builder {
        private boolean success = true;
        private String message = "";
        private List<Person> collection = null;
        private String info = null;
        private Object data = null;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder collection(List<Person> collection) {
            this.collection = collection;
            return this;
        }

        public Builder info(String info) {
            this.info = info;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Person> getCollection() {
        return collection;
    }

    public String getInfo() {
        return info;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", collection=" + (collection != null ? collection.size() : "null") +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}