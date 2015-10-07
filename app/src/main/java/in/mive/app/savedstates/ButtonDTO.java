package in.mive.app.savedstates;

import android.widget.Button;

/**
 * Created by shubham on 8/21/2015.
 */
public class ButtonDTO {

    private static ButtonDTO buttonDTO = new ButtonDTO();
    private ButtonDTO(){};

    private Button btn;

   public static  ButtonDTO getInstance()
    {
        return buttonDTO;
    }

   public void setBtn(Button btn)
    {
        this.btn = btn;
    }

   public Button getBtn()
    {
        return btn;
    }
}
