package nro.task;

import nro.clan.ClanTask;


public class TaskPlayer {

    public TaskMain taskMain;

    public SideTask sideTask;

    public ClanTask clanTask;
    
    public KolTask kolTask;
    
    public EventTask eventTask;

    public TaskPlayer() {
        this.eventTask = new EventTask();
        this.sideTask = new SideTask();
        this.clanTask = new ClanTask();
        this.kolTask = new KolTask();
    }

    public void dispose() {
        this.taskMain = null;
        this.sideTask = null;
        this.clanTask = null;
        this.eventTask = null;
        if (this.kolTask != null) {
            this.kolTask.dispose();
            this.kolTask = null;
        }
    }
}





