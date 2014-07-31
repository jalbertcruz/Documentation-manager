package documentation.manager;

public class DocEntry {
    String name, path;
    Long length;

    @Override
    public String toString() {
        return "DocEntry{" + "name=" + name + ", path=" + path + ", length=" + length + '}';
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DocEntry(String name, String path, Long length) {
        this.name = name;
        this.path = path;
        this.length = length;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
