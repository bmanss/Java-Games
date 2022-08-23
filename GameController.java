import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import BrickBreaker.BrickBreakerPanel;
import Pong.PongPanel;
import Solitaire.SolitairePanel;


public class GameController extends JFrame implements ActionListener{

    Action pauseMenu;
    MainMenu mainMenu;
    SolitairePanel solitaire;
    PongPanel pongPanel;
    BrickBreakerPanel brickBreaker;
    JPanel currentPanel;
    JButton button_Solitaire;
    JButton button_Pong;
    JButton button_Brick;
    JButton quitToMenu;
    JButton quitToDesktop;

    ActionListener pauseOptions;

    JRootPane rootPane = this.getRootPane();

    Container glassPanel;
    Container contentPane;


    GameController(){
        contentPane = getContentPane();

        mainMenu = new MainMenu();
        currentPanel = mainMenu;

        this.add(mainMenu);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);


        // setup pause menu button
        quitToMenu = new JButton("Quit To Menu");
        quitToMenu.setBounds(550, 325, 150, 75);
        
        // setup glass panel
        this.getRootPane().setGlassPane(new JComponent() {
            public void paintComponent(Graphics g){
                g.setColor(new Color(0,0,0,150));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        });
        glassPanel = (Container)this.getGlassPane();
        glassPanel.add(quitToMenu);

        // pauseMenu button behavior
        pauseOptions = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (currentPanel != mainMenu)
                    contentPane.remove(currentPanel);

                mainMenu.setVisible(true);
                glassPanel.setVisible(!glassPanel.isVisible());
                currentPanel = mainMenu;
                validate();
                repaint();
            }
        };
        quitToMenu.addActionListener(pauseOptions);
 
        // mouse adaptor for glass panel to interrupt mouse actions in game panels
        glassPanel.addMouseListener(new MouseInputAdapter() {});

        // setup keybinding to access paused menu options
        pauseMenu = new pauseAction();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0,true), "pauseMenu");
        rootPane.getActionMap().put("pauseMenu", pauseMenu);

        // setup main menu buttons
        button_Brick = new JButton("Brick Breaker");
        button_Solitaire = new JButton("Solitaire");
        button_Pong = new JButton("Pong");
        quitToDesktop = new JButton("Quit To Desktop");

        button_Solitaire.setBounds(550, 150, 150, 60);
        button_Pong.setBounds(550, 250, 150, 60);
        button_Brick.setBounds(550, 350, 150, 60);
        quitToDesktop.setBounds(550 , 450, 150, 60);

        button_Solitaire.addActionListener(this);
        button_Pong.addActionListener(this);
        button_Brick.addActionListener(this);
        quitToDesktop.addActionListener(this);

        mainMenu.add(quitToDesktop);
        mainMenu.add(button_Solitaire);
        mainMenu.add(button_Pong);
        mainMenu.add(button_Brick);

    }

    // actions to perform when menu buttons are clicked
    public void actionPerformed(ActionEvent e) {

        mainMenu.setVisible(false);
        if (e.getSource() == quitToDesktop)
            System.exit(1);

        if (e.getSource() == button_Solitaire){
            solitaire = new SolitairePanel();
            currentPanel = solitaire;
            this.add(solitaire);
        }
        if (e.getSource() == button_Pong){
            pongPanel = new PongPanel();
            currentPanel = pongPanel;
            this.add(pongPanel);
        }
        if (e.getSource() == button_Brick){
            brickBreaker = new BrickBreakerPanel();
            currentPanel = brickBreaker;
            this.add(brickBreaker);
        }
        
        currentPanel.grabFocus();
        validate();
        repaint();
    }

    // action to perform when escape key is pressed as long as the window is in focus
    public class pauseAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentPanel != mainMenu)
                glassPanel.setVisible(!glassPanel.isVisible());
        }
    }
}
    
