package nellaschat.coolapp_engine.it.nellaschat.client;

public abstract class ServerAbstact extends Thread {

    private ExternalServer es;

    public ServerAbstact(ExternalServer es){
        this.es = es;
    }

    public ServerAbstact() {
    }

    @Override
    public void run(){
        //Code
    }

    public boolean isReady(){
        return es.isReady();
    }

    public void invia(String s){
        es.invia(s);
    }

    public String ricevi(){
        return es.ricevi();
    }

    public void setExternalServer(ExternalServer es){
        this.es = es;
    }

    public ExternalServer getExternalServer(){
        return es;
    }
}
