package nellaschat.coolapp_engine.it.nellaschat.pattern;

public interface Observable {

    public void addObservable(Observer observer);

    public void removeObservable(Observer observer);

    public void clearObservables();

    public void notifyUpdateObservables();

}
