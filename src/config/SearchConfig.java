package config;

public class SearchConfig {

    Long from, stepSize;
    String connectString, user, password;

    boolean eliminarUltimosRepetidos, indexar, vaciarDB;

    public boolean isEliminarUltimosRepetidos() {
        return eliminarUltimosRepetidos;
    }

    public void setEliminarUltimosRepetidos(boolean eliminarUltimosRepetidos) {
        this.eliminarUltimosRepetidos = eliminarUltimosRepetidos;
    }

    public boolean isIndexar() {
        return indexar;
    }

    public void setIndexar(boolean indexar) {
        this.indexar = indexar;
    }

    public boolean isVaciarDB() {
        return vaciarDB;
    }

    public void setVaciarDB(boolean vaciarDB) {
        this.vaciarDB = vaciarDB;
    }
    
    public SearchConfig() {
    }

    public Long getStepSize() {
        return stepSize;
    }

    public void setStepSize(Long stepSize) {
        this.stepSize = stepSize;
    }

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SearchConfig(Long from) {
        this.from = from;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }
}
