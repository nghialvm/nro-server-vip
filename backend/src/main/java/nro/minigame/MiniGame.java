package nro.minigame;



public class MiniGame {
    
    private static MiniGame instance;
    public ConSoMayMan MiniGame_S1 = new ConSoMayMan(); // XoSo
    
    public static MiniGame gI()
    {
        if(instance == null)
        {
            instance = new MiniGame();
        }
        return instance;
    }
}






