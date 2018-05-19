/*
 * Ajay Raj Merchia
 * 3-27-15
 * EscapeFromMathMountain
*/

//import GUI classes for JFrames
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Scanner;
import java.util.Random;

public class EscapeFromMathMountain extends JFrame //make a JFrame that will be my game
{
    CardLayout mainCards;                       //declare CardLayout manager

    ///OverArching Panels
    ControlScreen main;                         //declare ControlScreen panel that will control the visible content
    StartPanel start;                           //declare StartPanel panel that will be the first screen displayed
    InstructionPanel instructions;              //declare InstructionPanel panel that will contain the instructions
    SettingsPanel settings;                     //declare SettingsPanel panel that will allow user to change settings
    GameScreen gamePlay;                        //declare GameScreen panel that will contain panels on which the game is played
    HackSettings hacks;
    HelpScreen help;
	ProfileScreen prof;

    ///global variables
    final int FRAME_WIDTH = 1000;
    final int FRAME_HEIGHT = 618;
    final boolean leftGame = true;
    boolean rightGame = false;
    Icon[] avatars;

    boolean[] levelBeat = new boolean[3];


	String[][] questions = new String[100][4];	//Question & 3 Answer CHoices in the rows, 100 questions/category
	Scanner questionsReader;
	PrintWriter userSaving;
	Scanner userReading;


    ///Images (Logo)
    Image logo = Toolkit.getDefaultToolkit().getImage("assets/images/logo.png");
    Color myGray = new Color(220,220,220);
    Color leftBlue = new Color(0,7,255);
	Color rightRed = new Color(255,0,0);

	///Settings Variables
	final int LEVEL_TO_TIME = 15;
	final int CATEGORY_TO_TIME = 10;
	int categoryDifficulty = 0;
	String category = "Arithmetic";
	int CPULevel = 1;	//Subtract from 5 & mult by 200 to get think time
    int thinkTime = (5-CPULevel)*LEVEL_TO_TIME + categoryDifficulty*CATEGORY_TO_TIME;
    boolean multiPlayer = false;
    String leftGameName = "Player 1";
    String rightGameName = "Computer";
    int leftGameAvatar = 0;
    int rightGameAvatar = 0;
    boolean leftGameHighlight = false;
    boolean rightGameHighlight = false;
    int leftGameStart = 1;
    int rightGameStart = 1;



    public static void main(String[] args)      //main method for JFrame
    {
         EscapeFromMathMountain application = new EscapeFromMathMountain();     //create an instance of the application
    }

    public EscapeFromMathMountain()             //constructor for JFrame
    {
        super("Escape From Math Mountain");     //call superMethod
        setSize(FRAME_WIDTH,FRAME_HEIGHT);      //set size to 1000 wide by 618 tall ~Phi
        setLocation(20,20);                    //set Location to 100,20
        setResizable(false);                    //make the app nonresizable
        main = new ControlScreen();             //make the ControlScreen
        setContentPane(main);                   //add the ControlScreen to the JFrame
        setVisible(true);                       //make the JFrame Visible
        setDefaultCloseOperation(EXIT_ON_CLOSE);	//close when done

    }

    class HackSettings extends JPanel
    {
		HackPanel hp = new HackPanel();

		JButton returnToStartMenuh = new JButton("Go Back to Start Menu");
		ActionListener backToStartListener = new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				mainCards.show(main,"Start");
				main.remove(hacks);
			}
		};

		public HackSettings()
		{
			setLayout(new BorderLayout());
			returnToStartMenuh.setPreferredSize(new Dimension(FRAME_WIDTH-50,50));
            returnToStartMenuh.addActionListener(backToStartListener);

            hp.setPreferredSize(new Dimension(1000,618-50));

            add(returnToStartMenuh,BorderLayout.SOUTH);//add Return to Start Menu Button to south
			add(hp,BorderLayout.CENTER);
		}

		class HackPanel extends JPanel
		{
			public HackPanel()
			{
				setLayout(new GridLayout(1,2,10,0));

				Hacks lh = new Hacks(true);
				Hacks rh = new Hacks(false);
				add(lh);
				add(rh);

			}

			class Hacks extends JPanel
			{
				boolean left; //true = left side, false = right side
				int localStartVal = 0;
				boolean localHigh = false;
				JLabel title;
				JSlider startVal;
				JLabel startDis;
				JButton highlight;


				public Hacks(boolean l)
				{
					setBackground(myGray);
					left = l;
					setLayout(null);

					///Title JLabel
					if(left)
					{
						title = new JLabel("Hacks For " + leftGameName,JLabel.CENTER);
						title.setForeground(leftBlue);
					}else
					{
						title = new JLabel("Hacks For " + rightGameName,JLabel.CENTER);
						title.setForeground(rightRed);
					}
					title.setBounds(0,20,495,50);
					title.setFont(new Font("",Font.BOLD,40));
					add(title);

					///JLabel to display Start Position
					startDis = new JLabel("Start at Tunnel: " + localStartVal);
					startDis.setBounds(0,100,495,70);
					startDis.setFont(new Font("",Font.BOLD,30));
					add(startDis);

					///Slider to Control Start Position
					if(left)
						startVal = new JSlider(SwingConstants.HORIZONTAL, 0, 20, leftGameStart);
					else
						startVal = new JSlider(SwingConstants.HORIZONTAL, 0, 20, rightGameStart);
					startVal.setBounds(0,200,495,100);
					startVal.addChangeListener(new ChangeListener(){
						public void stateChanged(ChangeEvent evt)
						{
							localStartVal = startVal.getValue();

							if (localStartVal == 0)
								startVal.setValue(1);

							if(left)
								leftGameStart =	localStartVal;
							else
								rightGameStart = localStartVal;



							startDis.setText("Start at Tunnel: " + localStartVal);

						}
					});
					startVal.setMinorTickSpacing(1);
					startVal.setMajorTickSpacing(5);
					startVal.setPaintTicks(true);
					startVal.setPaintLabels(true);
					startVal.setSnapToTicks(true);
					startVal.setBackground(myGray);

					add(startVal);


					///JButton to toggle highlight
					highlight = new JButton("Highlight Correct Tunnel");
					highlight.setBounds(97,350,301,50);
					highlight.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent evt)
						{
							localHigh = !localHigh;

							if(left)
								leftGameHighlight = localHigh;
							else
								rightGameHighlight = localHigh;

							if(localHigh)
								highlight.setText("Don't Highlight Correct Tunnel");
							else
								highlight.setText("Highlight Correct Tunnel");

							repaint();
						}
					});
					add(highlight);
				}

				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);

					//Draw the Cave
					g.setColor(new Color(179,91,0));
					g.fillRect(180,425,135,84);
					g.setColor(Color.BLACK);
					g.drawRect(180,425,135,84);
					if((left && leftGameHighlight) || (!left && rightGameHighlight))
						g.setColor(Color.GREEN);
					g.fillArc(180,425,135,84*2,0,180);
				}
			}

		}


	}

    class ControlScreen extends JPanel          //ControlScreen class (nested 1 deep)
    {
        public ControlScreen()                  //ControlScreen constructor
        {
            mainCards = new CardLayout();               //instantiate CardLayout Manager
            setLayout(mainCards);                       //setLayout of the main

            ///instantiate OverArching Panels (starting with the StartPanel)
            start = new StartPanel();
            settings = new SettingsPanel();
            instructions = new InstructionPanel();
            help = new HelpScreen();
            prof = new ProfileScreen();

            ///add OverArching Panels (starting with the StartPanel)
            add(start,"Start");
            add(settings,"Settings");
            add(instructions,"Instructions");
            add(help,"Help");
            add(prof,"Profiles");

            for(int i = 0; i < levelBeat.length; i++)
				levelBeat[i] = false;

        }



    }

    class ProfileScreen extends JPanel
    {
		ProfilePanel pp = new ProfilePanel();

		JButton returnToStartMenuh = new JButton("Go Back to Start Menu");
		ActionListener backToStartListener = new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				mainCards.show(main,"Start");
			}
		};

		public ProfileScreen()
		{
			setLayout(new BorderLayout());
			returnToStartMenuh.setPreferredSize(new Dimension(FRAME_WIDTH-50,50));
            returnToStartMenuh.addActionListener(backToStartListener);

            pp.setPreferredSize(new Dimension(1000,618-50));

            add(returnToStartMenuh,BorderLayout.SOUTH);//add Return to Start Menu Button to south
			add(pp,BorderLayout.CENTER);
		}

		class ProfilePanel extends JPanel
		{
			Scanner profileReader;
			JComboBox profileNames;
			String[] userList = new String[100];

			JLabel defaultLevelHeader = new JLabel("Category",JLabel.CENTER);
			JLabel defaultLevel = new JLabel("",JLabel.CENTER);
			JLabel levelsUnlockedHeader=new JLabel("Categories Unlocked",JLabel.CENTER);
			JLabel levelsUnlocked = new JLabel("",JLabel.CENTER);
			JLabel defaultCPUHeader=new JLabel("Computer Level",JLabel.CENTER);
			JLabel defaultCPU = new JLabel("",JLabel.CENTER);
			JLabel defaultModeHeader = new JLabel("Mode",JLabel.CENTER);
			JLabel defaultMode = new JLabel("",JLabel.CENTER);

			public ProfilePanel()
			{
				setLayout(null);
				getProfiles();

				///Set Up JComboBox
				profileNames.setBounds(300,0,400,50);
				profileNames.setFont(new Font("",Font.PLAIN,30));
				profileNames.setAlignmentY(JComboBox.CENTER_ALIGNMENT);
				profileNames.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent evt)
					{
						for(int i=0; i < levelBeat.length;i++)
							levelBeat[i] = false;
						getUserInfo();
					}
				});
				add(profileNames);

				///Set JLabel Fonts
				Font header = new Font("",Font.BOLD,40);
				Font subheader=new Font("",Font.ITALIC,30);
				Font text=new Font("",Font.PLAIN,20);

				defaultLevelHeader.setFont(header);
				defaultCPUHeader.setFont(header);
				levelsUnlockedHeader.setFont(subheader);
				defaultModeHeader.setFont(header);

				defaultLevel.setFont(text);
				levelsUnlocked.setFont(text);
				defaultMode.setFont(text);
				defaultCPU.setFont(text);

				///Set Component Locations
				defaultLevelHeader.setBounds(25,250,300,50);
				defaultCPUHeader.setBounds(350,250,300,50);
				defaultModeHeader.setBounds(675,250,300,50);

				defaultLevel.setBounds(25,300,300,50);
				defaultCPU.setBounds(350,300,300,50);
				defaultMode.setBounds(675,300,300,50);

				levelsUnlockedHeader.setBounds(25,400,300,50);
				levelsUnlocked.setBounds(25,450,300,50);

				add(defaultCPU);
				add(defaultCPUHeader);
				add(defaultLevel);
				add(defaultLevelHeader);
				add(defaultMode);
				add(defaultModeHeader);
				add(levelsUnlocked);
				add(levelsUnlockedHeader);

				repaint();
			}

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(Toolkit.getDefaultToolkit().getImage("assets/images/avatars/f"+leftGameAvatar+".png"),450,150,75,75,this);
			}
			public void getUserInfo()
			{
				try{
					profileReader = new Scanner(new File("assets/text/Users/"+userList[profileNames.getSelectedIndex()]+".txt"));
				}catch(FileNotFoundException exc)
				{
					System.out.println("File Not Found");
					System.exit(1);
				}

				leftGameName = userList[profileNames.getSelectedIndex()];

				category = profileReader.nextLine();
				defaultLevel.setText(category);

				levelsUnlocked.setText(profileReader.nextLine());
				String howManyLevels = levelsUnlocked.getText();
				int oldIndex = 0;
				int commas = 0;
				while(howManyLevels.indexOf(",",oldIndex+1) >0)
				{
					commas++;
					oldIndex = howManyLevels.indexOf(",",oldIndex+1);
				}

				for(int i = 0; i < commas; i++)
					levelBeat[i] = true;

				defaultCPU.setText(profileReader.nextLine());
				if(defaultCPU.getText() == "Easy")
					CPULevel = 1;
				if(defaultCPU.getText() == "Normal")
					CPULevel = 2;
				if(defaultCPU.getText() == "Hard")
					CPULevel = 3;
				if(defaultCPU.getText() == "Exteme")
					CPULevel = 4;

				defaultMode.setText(profileReader.nextLine());
				if(defaultMode.getText() == "Player Vs. Computer")
					multiPlayer = true;
				else
				{
					multiPlayer = false;
				}

				leftGameAvatar = profileReader.nextInt() + 1;
			}

			public void getProfiles()
			{
				try{
					profileReader = new Scanner(new File("assets/text/Users/userList.txt"));
				}catch(FileNotFoundException exc)
				{
					System.out.println("File Not Found");
					System.exit(1);
				}

				int i = 0;


				do
				{
					userList[i] = profileReader.nextLine();
					i++;
				}while(profileReader.hasNextLine());

				String[] trimmedUserList = new String[i];

				for(int x = 0; x < trimmedUserList.length; x++)
					trimmedUserList[x] = userList[x];

				profileNames = new JComboBox(trimmedUserList);

			}

		}
	}

    class StartPanel extends JPanel             //StartPanel class (nested 1 deep)
    {
        ///global variables

        ///JButtons for Settings, Start, & Instructors ActionListener Overrides in the Constructor
        JButton goToSettings = new JButton("Settings");
        JButton startGame = new JButton("Start!");
        JButton goToInstructions = new JButton("Instructions");
        JButton saveGame = new JButton("Save Progress & Settings");

        ///JButton to quit (quit using System.exit(1);)
        JButton quit = new JButton("Quit!");

        ///JLabels for Settings Display (category, Level, Play Type) <-- May not be done

		PrintWriter saveFile;
		Scanner userListReader;

        public StartPanel()                     //Constructor for the StartPanel
        {
			///Set UP the Panel
            setLayout(null);	//setNullLayout
			setBorder( BorderFactory.createEtchedBorder() );
			setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT) );


            ///Add the JLabels to the Panel <-- May not be done in final

            ///Add the player avatars to the Panel <-- May not be done in final

            ///Add the Buttons to the Panel
            goToSettings.setBounds(25,(int)(.75*FRAME_HEIGHT),300,50);
            goToSettings.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt)
				{
					mainCards.show(main,"Settings");

					if(levelBeat[0])
					{
						settings.cat.alg1.setEnabled(true);
						settings.cat.activated[0] = true;
					}if(levelBeat[1]){
						settings.cat.alg2.setEnabled(true);
						settings.cat.activated[1] = true;
					}if(levelBeat[2]){
						settings.cat.trig.setEnabled(true);
						settings.cat.activated[2] = true;
					}
				}
			});
			this.add(goToSettings);

			startGame.setBounds(350,(int)(.75*FRAME_HEIGHT),300,50);
			startGame.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt)
				{
					gamePlay = new GameScreen();
					main.add(gamePlay, "The Game");
					mainCards.show(main,"The Game");
				}
			});
			this.add(startGame);

			goToInstructions.setBounds(675,(int)(.75*FRAME_HEIGHT),300,50);
            goToInstructions.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt)
				{
					mainCards.show(main,"Instructions");
				}
			});
			this.add(goToInstructions);

            quit.setBounds(25,(int)(.75*FRAME_HEIGHT)+75,950,50);
            quit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt)
				{
					System.exit(1);
				}
			});
            this.add(quit);

            saveGame.setBounds(800,0,200,40);
            saveGame.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt)
				{
					setPrinter();

					saveFile.println(category);

					String lvls = "Arithmetic";
					if(levelBeat[0])
						lvls = lvls + ", Algebra 1";
					if(levelBeat[1])
						lvls = lvls + ", Algebra 2";
					if(levelBeat[2])
						lvls = lvls + ", Trigonometry";

					saveFile.println(lvls);

					if(CPULevel == 1)
						saveFile.println("Easy");
					if(CPULevel == 2)
						saveFile.println("Normal");
					if(CPULevel == 3)
						saveFile.println("Hard");
					if(CPULevel == 4)
						saveFile.println("Extreme");

					if(multiPlayer)
						saveFile.println("Player Vs. Player");
					else
						saveFile.println("Player Vs. Computer");

					saveFile.println(leftGameAvatar);

					saveFile.close();
				}
			});
            this.add(saveGame);

        }

        public void setPrinter()
        {
			try{
				userListReader = new Scanner(new File("assets/text/Users/userList.txt"));
			}catch(FileNotFoundException e){
					System.out.println("FileNF");
				}

			String newList="";
			String next = "";
			while(userListReader.hasNextLine())
			{
				next = userListReader.nextLine();
				if(!(next.equals(leftGameName)))
					newList = newList + next + ",";


			}


			newList = newList + leftGameName + ",";

			try
			{
				saveFile = new PrintWriter(new File("assets/text/Users/userList.txt"));
			}catch(Exception e){}

			int oldIndex = 0;
			while(newList.indexOf(",",oldIndex)>0)
			{
				saveFile.println(newList.substring(oldIndex,newList.indexOf(",",oldIndex)));
				oldIndex = newList.indexOf(",",oldIndex) + 1;

			}
			saveFile.close();

			try
			{
				saveFile = new PrintWriter(new File("assets/text/Users/"+leftGameName+".txt"));
			}catch(Exception e){}


		}

        public void paintComponent(Graphics g)
        {
			setBackground(myGray);	//set background to gray
			super.paintComponent(g);

            g.drawImage(logo,100,0,800,400,this);///Add the Logo to the top 1/2 ish of Panel

		}

    }

    class HelpScreen extends JPanel
    {
		JButton returnToStartMenu = new JButton("Go Back to Start Menu");
		ActionListener backToStartListener = new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				mainCards.show(main,"Start");
			}
		};

		Help me = new Help();

		public HelpScreen(){
			setLayout(new BorderLayout());//set BorderLayout

            //Add the return to start Menu button
            returnToStartMenu.setPreferredSize(new Dimension(FRAME_WIDTH-50,50));
            returnToStartMenu.addActionListener(backToStartListener);

            add(returnToStartMenu,BorderLayout.SOUTH);//add Return to Start Menu Button to south
            add(me,BorderLayout.CENTER);//add Instructions Panel to center
		}

		class Help extends JPanel
		{
			public Help(){}

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(Toolkit.getDefaultToolkit().getImage("assets/images/operandHelp.jpg"),0,0,FRAME_WIDTH,FRAME_HEIGHT-50,this);
			}

		}

	}

    class InstructionPanel extends JPanel        //InstructionPanel class (nested 1 deep)
    {
        Instructions instr = new Instructions();//Make Instance of Instructions
        ///Create the Return to Start Menu Button (And its actionlistener)

		JButton returnToStartMenu = new JButton("Go Back to Start Menu");
		ActionListener backToStartListener = new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				mainCards.show(main,"Start");
			}
		};




        public InstructionPanel(){              //Constructor for the InstructionPanel

            setLayout(new BorderLayout());//set BorderLayout

            //Add the return to start Menu button
            returnToStartMenu.setPreferredSize(new Dimension(FRAME_WIDTH-50,50));
            returnToStartMenu.addActionListener(backToStartListener);

            add(returnToStartMenu,BorderLayout.SOUTH);//add Return to Start Menu Button to south
            add(instr,BorderLayout.CENTER);//add Instructions Panel to center

        }

        class Instructions extends JPanel implements MouseListener, KeyListener		//Instructions Graphic Class (nested 2 deep)
        {
			///Declare All Instruction Text Related Global Variables
			JTextArea objective,penalty;
			String objectWord,penalWord;
			Scanner scans;
			boolean[] unlockHacks = new boolean[4];	//Four Step Procedure: Click on the Logo, Type A, Type &, Type R

			JButton goToHelp = new JButton("Need Help with the Question Format?");
			ActionListener helpListener = new ActionListener(){
				public void actionPerformed(ActionEvent evt)
				{
					mainCards.show(main,"Help");
				}
			};

        	public Instructions()				//Constructor
        	{
				setLayout(null);				//nulllayout
				getText();						//Get the instructions fromt their files

				///Make new text aresas given their content
				objective = new JTextArea(objectWord);
				penalty = new JTextArea(penalWord);

				///Add the Go To Help Screen
				goToHelp.setBounds(350,400,300,50);
				goToHelp.addActionListener(helpListener);
				add(goToHelp);

				///Add and make the textArea pretty
				objective.setBounds(30,220,950,70);
				objective.setLineWrap(true);
				objective.setWrapStyleWord(true);
				objective.setBackground(myGray);
				objective.setFont(new Font("",Font.PLAIN,15));
				objective.setEditable(false);
				add(objective);

				penalty.setBounds(30,320,950,40);
				penalty.setLineWrap(true);
				penalty.setWrapStyleWord(true);
				penalty.setBackground(myGray);
				penalty.setFont(new Font("",Font.PLAIN,15));
				penalty.setEditable(false);
				add(penalty);



				addMouseListener(this);
				addKeyListener(this);

				for(int i = 0; i < unlockHacks.length; i++)
					unlockHacks[i] = false;

			}

			public void getText()
			{
				///Get the Objective of the Game and save to a String
				try
				{
					scans = new Scanner(new File("assets/text/Objective.txt"));
				}catch(Exception e){}

				objectWord = scans.nextLine();


				///Get the Penaltites the Game and save to a String
				try
				{
					scans = new Scanner(new File("assets/text/Penalties.txt"));
				}catch(Exception e){}

				penalWord = scans.nextLine();



			}

        	public void paintComponent(Graphics g)	//Graphics method to draw the Instructions Panel in
        	{
				setBackground(myGray);
        		super.paintComponent(g);		//call the superMethod

        		//draw the Title
        		g.drawImage(logo,300,0,400,200,this);

        		g.setFont(new Font("",Font.BOLD + Font.ITALIC,20));
        		///draw Bullet Point & Text for Goal/Penalties/How to Move
        		g.drawString("Objective",5,210);
        		g.drawString("Penalties",5,310);
        		g.drawString("How to Move",5,380);
        		///Draw Images Regarding How to Move
        		g.setFont(new Font("",Font.BOLD,20));
        		g.drawImage(Toolkit.getDefaultToolkit().getImage("assets/images/keyboard.png"),175,375,150+175,100+375,0,0,165,110,this);
        		g.drawString("Player 1",210,495);
        		g.drawImage(Toolkit.getDefaultToolkit().getImage("assets/images/keyboard.png"),175+500,375,150+175+500,100+375,165,0,330,110,this);
				g.drawString("Player 2",710,495);

        	}

        	public boolean inBetween(int val, int lowBound, int upBound)
			{
				if(val > lowBound && val < upBound)
					return true;
				else
				{
					return false;
				}

			}


			public void unlockTheHacks()
			{
				for(int i = 0; i < unlockHacks.length; i++)
					unlockHacks[i] = false;

				hacks = new HackSettings();
				main.add(hacks,"Hack");
				mainCards.show(main,"Hack");


			}

        	public void mousePressed(MouseEvent e){}
			public void mouseReleased(MouseEvent e){}
			public void mouseClicked(MouseEvent e)
			{
				int x,y;
				x = e.getX();
				y = e.getY();

				requestFocus();

				if(inBetween(x,300,700)&&inBetween(y,0,200))
					unlockHacks[0] = true;



			}
			public void mouseEntered(MouseEvent e){}
			public void mouseExited(MouseEvent e){}


			public void keyPressed(KeyEvent e){}
			public void keyReleased(KeyEvent e){}
			public void keyTyped(KeyEvent e)
			{
				char c = e.getKeyChar();

				if(unlockHacks[0] && c == 'A')
					unlockHacks[1] = true;
				if(unlockHacks[0] && unlockHacks[1] && c == '&')
					unlockHacks[2] = true;
				if(unlockHacks[0] && unlockHacks[1] && unlockHacks[2] && c == 'R')
				{
					unlockHacks[3] = true;
					unlockTheHacks();
				}



			}

        }
    }

    class SettingsPanel extends JPanel          //SettingsPanel class (nested 1 deep)
    {
        ///Global Variables

		///Make Instances of Each Panel the Settings Panel contains
        Category cat = new Category();
        Level lev = new Level();
        Mode mod = new Mode();

		///Create the Return to Start Menu Button (And its actionlistener)
			JButton returnToStartMenu = new JButton("Go Back to Start Menu");
			ActionListener backToStartListener = new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				if(multiPlayer)
				{
					leftGameName = mod.player1Ent.getText();
					rightGameName = mod.player2Ent.getText();
				}else
				{
					leftGameName = mod.playerEnt.getText();
					rightGameName = mod.computerEnt.getText();
				}

				mainCards.show(main,"Start");
			}
		};



        public SettingsPanel()                  //Constructor for the SettingsPanel
        {
            setLayout(new BorderLayout(10,10));//set BorderLayout

            //add Return to Start Menu Button
             returnToStartMenu.addActionListener(backToStartListener);
			returnToStartMenu.setPreferredSize(new Dimension(FRAME_WIDTH,50));
			add(returnToStartMenu,BorderLayout.SOUTH);

            ///Add the Panels to their respective Borders
            add(cat,BorderLayout.WEST);
            add(mod,BorderLayout.EAST);
            add(lev,BorderLayout.CENTER);



        }

        class Category extends JPanel implements MouseMotionListener           //Category Class (nested 2 deep)
        {
            ///JRadioButtons & Their Actionlisteners
			ButtonGroup levelSelect = new ButtonGroup();

			JRadioButton arit = new JRadioButton("Arithmetic",true);
            JRadioButton alg1 = new JRadioButton("Algebra 1");
            JRadioButton alg2 = new JRadioButton("Algebra 2");
            JRadioButton trig = new JRadioButton("Trigonometry");

			int overNum = 0;
			boolean[] activated = new boolean[3];
			String[] levels = {"Arithmetic","Algebra 1", "Algebra 2", "Trigonometry"};

			JLabel line1 = new JLabel("",JLabel.CENTER);
			JLabel line2 = new JLabel("",JLabel.CENTER);

			//ActionListener that assigns String categry to the button selected
            ActionListener buttonResponder = new ActionListener(){
					public void actionPerformed(ActionEvent evt)
					{
						if(arit.isSelected())
						{
							category = arit.getText();
							categoryDifficulty = 0;
						}else if(alg1.isSelected())
						{
							category = alg1.getText();
							categoryDifficulty = 1;
						}else if(alg2.isSelected())
						{
							category = alg2.getText();
							categoryDifficulty = 2;
						}else if(trig.isSelected())
						{
							category = trig.getText();
							categoryDifficulty = 3;
						}

						thinkTime = (5-CPULevel)*LEVEL_TO_TIME + categoryDifficulty*CATEGORY_TO_TIME;

					}
				};

            ///JLabel that is the header of this panel
            JLabel header = new JLabel("Math Type");

            public Category()                   //Constructor for the Category Adjustment Settings Panel
            {
				setBackground(myGray);
                setLayout(new FlowLayout(FlowLayout.CENTER,333,40));//set To Flow Layout with vertical spacing
                addMouseMotionListener(this);

                setPreferredSize(new Dimension((FRAME_WIDTH-40)/3,FRAME_HEIGHT-50));//set Preffered Size to 1/3 of the width, and the remaining height

                header.setFont(new Font("hi",Font.BOLD,40));

                ///Add the JRadioButtons to their group
                levelSelect.add(arit);
				levelSelect.add(alg1);
				levelSelect.add(alg2);
				levelSelect.add(trig);

                ///Make Fonts Nicer
                Font buttonFont = new Font("hi",Font.PLAIN,20);
                arit.setFont(buttonFont);
                alg1.setFont(buttonFont);
                alg2.setFont(buttonFont);
                trig.setFont(buttonFont);

                ///Make Colors Blend in with back
                arit.setBackground(myGray);
                alg1.setBackground(myGray);
                alg2.setBackground(myGray);
                trig.setBackground(myGray);

                ///Add the ActionListnere to the JRadioButtons
                arit.addActionListener(buttonResponder);
                alg1.addActionListener(buttonResponder);
                alg2.addActionListener(buttonResponder);
                trig.addActionListener(buttonResponder);

                ///Make Locked Levels Unenabled
                alg1.setEnabled(false);
                alg2.setEnabled(false);
                trig.setEnabled(false);

                //Set Other Label Fonts
                line1.setFont(new Font("", Font.PLAIN,23));
                line2.setFont(new Font("", Font.PLAIN,15));
                line1.setVisible(false);
                line2.setVisible(false);

                //Add the JLabel to the Panel
                add(header);
                ///Add the JRadio Buttons to the Panel
                add(arit);
                add(alg1);
                add(alg2);
                add(trig);

                //Add the lines
                add(line1);
                add(line2);

                for(int i = 0; i < activated.length;i++)
					activated[i] = false;
            }

			public void updateHelp()
			{



				if(overNum!=0 && !activated[overNum-1])
				{
					line1.setVisible(true);
					line2.setVisible(true);

					line1.setText("To Unlock " + levels[overNum] + "...");
					line2.setText("Beat " + levels[overNum-1] + " Against the Computer");
				}else
				{
					line1.setVisible(false);
					line2.setVisible(false);
				}


			}

			public void mouseMoved(MouseEvent evt)
			{
				int x = evt.getX();
				int y = evt.getY();


				if(y >= alg1.getY()-10 && y <= alg1.getY()+alg1.getHeight()+10)
					overNum = 1;
				else if(y >= alg2.getY()-10 && y <= alg2.getY()+alg2.getHeight()+10)
					overNum = 2;
				else if(y >= trig.getY()-10 && y <= trig.getY()+trig.getHeight()+10)
					overNum = 3;
				else
					overNum = 0;


				updateHelp();
			}
			public void mouseDragged(MouseEvent evt){}


        }

        class Level extends JPanel              //Level Class (nested 2 deep)
        {
           ButtonGroup speedSelect = new ButtonGroup();

			JRadioButton easy = new JRadioButton("Easy",true);
            JRadioButton norm = new JRadioButton("Normal");
            JRadioButton hard = new JRadioButton("Hard");
            JRadioButton extr = new JRadioButton("Extreme");


            ActionListener buttonResponder = new ActionListener(){
					public void actionPerformed(ActionEvent evt)
					{
						if(easy.isSelected())
							CPULevel = 1;
						else if(norm.isSelected())
							CPULevel = 2;
						else if(hard.isSelected())
							CPULevel = 3;
						else if(extr.isSelected())
							CPULevel = 4;

						thinkTime = (5-CPULevel)*LEVEL_TO_TIME + categoryDifficulty*CATEGORY_TO_TIME;


					}
				};

            ///JLabel that is the header of this panel
            JLabel header = new JLabel("Computer Level");


            public Level()                   //Constructor for the Level Adjustment Settings Panel
            {
				setBackground(myGray);
                setLayout(new FlowLayout(FlowLayout.CENTER,333,40));//set To Flow Layout with vertical spacing

                setPreferredSize(new Dimension((FRAME_WIDTH-40)/3,FRAME_HEIGHT-50));//set Preffered Size to 1/3 of the width, and the remaining height

                header.setFont(new Font("hi",Font.BOLD,40));

                ///Add the JRadioButtons to their group
                speedSelect.add(easy);
				speedSelect.add(norm);
				speedSelect.add(hard);
				speedSelect.add(extr);

                ///Make Colors Blend in with back
                easy.setBackground(myGray);
                norm.setBackground(myGray);
                hard.setBackground(myGray);
                extr.setBackground(myGray);

                ///Make Fonts Nicer
                Font buttonFont = new Font("hi",Font.PLAIN,20);
                easy.setFont(buttonFont);
                norm.setFont(buttonFont);
                hard.setFont(buttonFont);
                extr.setFont(buttonFont);

                ///Add ActionListener to Buttons
                easy.addActionListener(buttonResponder);
                norm.addActionListener(buttonResponder);
                hard.addActionListener(buttonResponder);
                extr.addActionListener(buttonResponder);

                //Add the JLabel to the Panel
                add(header);
                ///Add the JRadio Buttons to the Panel
                add(easy);
                add(norm);
                add(hard);
                add(extr);
            }

        }

        class Mode extends JPanel               //Mode Class (nested 2 deep)
        {
            CardLayout modeManager = new CardLayout();//Create a CardLayout

            ///ActionListener for JComboBoxes (gets the index Selected
            ActionListener leftListener = new ActionListener()
            {
				public void actionPerformed(ActionEvent evt)
				{
					JComboBox cb = (JComboBox)evt.getSource();
					leftGameAvatar = cb.getSelectedIndex();
				}
			};

			ActionListener rightListener = new ActionListener()
            {
				public void actionPerformed(ActionEvent evt)
				{
					JComboBox cb = (JComboBox)evt.getSource();
					rightGameAvatar = cb.getSelectedIndex();
				}
			};

            ///JComboBoxDeclarations
            JComboBox playerAva,computAva,player1Ava,player2Ava;

           ///JTextboxes to type names
            JTextField playerEnt = new JTextField("Player 1",40);
            JTextField computerEnt = new JTextField("Computer",40);
            JTextField player1Ent = new JTextField("Player 1",40);
            JTextField player2Ent = new JTextField("Player 2",40);



            public Mode()                   //Constructor for the Category Adjustment Settings Panel
            {
				setBackground(myGray);	//set to professional backgroubnd
				setLayout(modeManager);//set To CardLayout

                avatars = new ImageIcon[16];			//make an array of icons for the combo box

                ///Add images to that array
				for(int i = 0; i<16; i++)
				{
					avatars[i] = new ImageIcon(Toolkit.getDefaultToolkit().getImage("assets/images/avatars/f"+(i+1)+".png"));
				}

                //Set up dimesnsions
               setPreferredSize(new Dimension((FRAME_WIDTH-40)/3,FRAME_HEIGHT-50));//set Preffered Size to 1/3 of the width, and the remaining height

                ///Add the panels relating to this
                PlayerVCompSettings pvc = new PlayerVCompSettings();//Create an Instance of PlayerVCompSettings
                PlayerVPlayerSettings pvp = new PlayerVPlayerSettings();//Create an Instnace of PlayerVPlayerSettings

                add(pvc,"pvc");//Add PlayerVCompSettings
                add(pvp,"pvp");//AddPlayerVPlayerSettings

            }


            class PlayerVCompSettings extends JPanel    //PlayerVCompSettings (nested 3 deep)
            {
                ///Global Variables

                ///JLabels to title this panel and Prompt the name textboxes
                JLabel header = new JLabel("User vs. Comp");
                JLabel player = new JLabel("Player 1");
                JLabel comput = new JLabel("Computer");



                ///JButton to set names & toggle mode & Respective ActionListneres
                JButton toggle = new JButton("Switch to Player Vs. Player");
                JButton setNames = new JButton("Load A Profile");



                public PlayerVCompSettings()
                {
					setBackground(myGray);;
                    ///set Layout to Null
                    setLayout(null);
                    ///Add the JLabel Title
                    header.setFont(new Font("hi",Font.BOLD, 40));
                    header.setBounds(20,40,280,50);
                    add(header);

                    ///Add the toggle button
                    toggle.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent evt)
							{
								///Swap modes and chagne its respective boolean
								modeManager.show(mod,"pvp");
								multiPlayer = !multiPlayer;
								player1Ava.setSelectedIndex(leftGameAvatar);
								player2Ava.setSelectedIndex(rightGameAvatar);
								///Make the Level stuff fade out
								lev.setBackground(Color.BLACK);
								lev.easy.setVisible(false);
								lev.norm.setVisible(false);
								lev.hard.setVisible(false);
								lev.extr.setVisible(false);
								lev.header.setVisible(false);

								rightGameName = player2Ent.getText();
								player2Ent.setText(rightGameName);
								player1Ent.setText(leftGameName);

							}
						});
                    toggle.setBounds(10,100,295,20);
                    add(toggle);

                      ///JComboBoxes to select avatars
					playerAva = new JComboBox(avatars);
					computAva = new JComboBox(avatars);


                    ///JLabel for Player
					player.setFont(new Font("H",Font.PLAIN,20));
					player.setBounds(10,140,100,30);
					add(player);

                    ///JTextbox for Player
                    playerEnt.setBounds(70,190,200,25);
                    add(playerEnt);

                    ///JComboBox for PlayerAvatar
                    playerAva.setBounds(120,220,100,75);
                    playerAva.addActionListener(leftListener);
                    add(playerAva);

                    ///JLabel for Comp
					comput.setFont(new Font("H",Font.PLAIN,20));
					comput.setBounds(10,300,100,30);
					add(comput);

                    ///JTextbox for Comp
                    computerEnt.setBounds(70,350,200,25);
                    add(computerEnt);

                    ///JComboBox for CompAva
                    computAva.setBounds(120,380,100,75);
                    computAva.addActionListener(rightListener);
                    add(computAva);

                    ///JButton to Update All Strings
                    setNames.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent evt)
							{
								mainCards.show(main,"Profiles");
							}
						});
					setNames.setBounds(10,465,295,50);
					add(setNames);
                }
            }

            class PlayerVPlayerSettings extends JPanel    //PlayerVPlayerSettings (nested 3 deep)
            {
                ///Global Variables

                ///JLabels to title this panel and Prompt the name textboxes
                JLabel header = new JLabel("User vs. User");
                JLabel player1 = new JLabel("Player 1");
                JLabel player2 = new JLabel("Player 2");


                ///JButton to set names & toggle mode & Respective ActionListneres
                JButton toggle = new JButton("Switch to Player Vs. Computer");
                JButton setNames = new JButton("Set Names");

                public PlayerVPlayerSettings()
                {
					setBackground(myGray);
                    ///set Layout to Null
                    setLayout(null);
                    ///Add the JLabel Title
                    header.setFont(new Font("hi",Font.BOLD, 40));
                    header.setBounds(20,40,280,50);
                    add(header);

                    ///Add the toggle button
                    toggle.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent evt)
							{
								///Switch apenl and make the mode switch accordingly
								modeManager.show(mod,"pvc");
								multiPlayer = !multiPlayer;

								playerAva.setSelectedIndex(leftGameAvatar);
								computAva.setSelectedIndex(rightGameAvatar);

								///Make other stuff blend in
								lev.setBackground(myGray);
								lev.easy.setVisible(true);
								lev.norm.setVisible(true);
								lev.hard.setVisible(true);
								lev.extr.setVisible(true);
								lev.header.setVisible(true);

								///Set Name to Computer
								rightGameName = "Computer";
								computerEnt.setText(rightGameName);
								playerEnt.setText(leftGameName);

							}
						});
                    toggle.setBounds(10,100,295,20);
                    add(toggle);

                    ///JComboBoxes to select avatars
					player1Ava = new JComboBox(avatars);
					player2Ava = new JComboBox(avatars);

                    ///JLabel for Player
					player1.setFont(new Font("H",Font.PLAIN,20));
					player1.setBounds(10,140,100,30);
					add(player1);

                    ///JTextbox for Player
                    player1Ent.setBounds(70,190,200,25);
                    add(player1Ent);

                    ///JComboBox for PlayerAvatar
                    player1Ava.setBounds(120,220,100,75);
                    player1Ava.addActionListener(leftListener);
                    add(player1Ava);

                    ///JLabel for Comp
					player2.setFont(new Font("H",Font.PLAIN,20));
					player2.setBounds(10,300,100,30);
					add(player2);

                    ///JTextbox for Comp
                    player2Ent.setBounds(70,350,200,25);
                    add(player2Ent);

                    ///JComboBox for CompAva
                    player2Ava.setBounds(120,380,100,75);
                    player2Ava.addActionListener(rightListener);
                    add(player2Ava);

                    ///JButton to Update All Strings
                    setNames.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent evt)
							{

							}
						});
					setNames.setBounds(10,465,295,50);
					add(setNames);
                }
            }

        }

    }

    class GameScreen extends JPanel implements KeyListener           //GameScreen class (nested 1 deep)
    {
		///Colors used in this Panel
		Color floor = new Color(255,197,144);
		Color brown = new Color(179,91,0);


		///Global Variables Applicable to the Entire Panel
		final int TRANSLATION_CONSTANT = 550;	//X translation to make the right screen
		final int AVATAR_SIZE = 40;				//Size of the Avatars
		final int PROGRESS_CIRCLE_MOVE = 25;	//verticle movement of the circle
		Image[] drawAvatar;
		boolean[] motion = new boolean[8]; //0 = w, 1 = s, 2 = a, 3 = d, 4 = up, 5 = down, 6 = left, 7 = right
		Timer moveIcon;
		boolean canMove;
		JLabel winner, loser;
		boolean endState = false;
		Timer blinker;


		///Left Gamer Attributes
		int leftXpos = 185;
		int leftYpos = 500;
		int [] leftQuestionOrder = new int[100];
		int leftQuestionsAnswered = 0;
		int leftTunnelsPassed = leftGameStart;
		int lcorrectCave = 3;
		Image leftFace;
		JLabel[] lcave = new JLabel[3];

		///Right Gamer Attributes
		int rightXpos = leftXpos + TRANSLATION_CONSTANT;
		int rightYpos = 500;
		int [] rightQuestionOrder = new int[100];
		int rightQuestionsAnswered = 0;
		int rightTunnelsPassed = rightGameStart;
		int rcorrectCave = 3;
		Image rightFace;
		JLabel[] rcave = new JLabel[3];
		Timer compThink;

		int timeSpentFlashing = 0;

        public GameScreen()                     //Constructor for the GameScreen
        {
			///Set Layout & Add my Quit Button
			setLayout(null);
			JButton quit = new JButton("Quit");
			quit.setBounds(450,550,100,50);
			quit.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent evt)
					{
						mainCards.show(main,"start");
						main.remove(gamePlay);
						if(!multiPlayer)
						compThink.stop();
					}
				});
			add(quit);


        	///Initialize the Game Given Settings
        	addKeyListener(this);	//add a keyListneer
        	makeQuestions(category);//make the Questions Arrays
        	canMove = true;

        	drawAvatar = new Image[16];
				for(int i = 0; i<16; i++)
				{
					drawAvatar[i] = Toolkit.getDefaultToolkit().getImage("assets/images/avatars/f"+(i+1)+".png");
				}
        	leftFace = drawAvatar[leftGameAvatar];
        	rightFace = drawAvatar[rightGameAvatar];
        	leftQuestionOrder = randomizeOrder(leftQuestionOrder,0,99);
        	rightQuestionOrder = randomizeOrder(rightQuestionOrder,0,99);

        	//Initialzie the JLabels
        	for(int i = 1; i < 4; i++)
        	{
				lcave[i-1] = new JLabel(questions[leftQuestionOrder[leftQuestionsAnswered]][i],JLabel.CENTER);
				rcave[i-1] = new JLabel(questions[rightQuestionOrder[rightQuestionsAnswered]][i],JLabel.CENTER);
        	}

        	//SetBounds for JLabels
        	for(int i = 1; i < 4; i++)
			{
				lcave[i-1].setBounds(12*i+134*(i-1)+12,150,110,40);
				rcave[i-1].setBounds(12*i+134*(i-1)+562,150,110,40);

				lcave[i-1].setForeground(Color.WHITE);
				rcave[i-1].setForeground(Color.WHITE);

				lcave[i-1].setFont(new Font("",Font.BOLD,30));
				rcave[i-1].setFont(new Font("",Font.BOLD,30));

				if(category.equals("Algebra 2"))
				{
					lcave[i-1].setFont(new Font("",Font.BOLD,12));
					rcave[i-1].setFont(new Font("",Font.BOLD,12));
				}

        	}

        	//add JLabels
        	for(int i = 0; i < 3; i++)
        	{
				add(lcave[i]);
				add(rcave[i]);
			}


			//Write Names at the Bottom in their base boxes!
			JLabel leftName = new JLabel(leftGameName,JLabel.CENTER);
			leftName.setFont(new Font("",Font.BOLD,35));
			leftName.setForeground(leftBlue);
			leftName.setBounds(0,550,450,40);
			add(leftName);

			JLabel rightName = new JLabel(rightGameName,JLabel.CENTER);
			rightName.setFont(new Font("",Font.BOLD,35));
			rightName.setForeground(rightRed);
			rightName.setBounds(TRANSLATION_CONSTANT,550,450,40);
			add(rightName);

			///Add the JLabels and setInvisible
			winner = new JLabel("You Win!",JLabel.CENTER);
			loser =  new JLabel("You Lose!",JLabel.CENTER);

			winner.setForeground(leftBlue);
			loser.setForeground(rightRed);

			winner.setVisible(endState);
			loser.setVisible(endState);

			winner.setFont(new Font("",Font.BOLD,35));
			loser.setFont(new Font("",Font.BOLD,35));

			winner.setBounds(0,300,450,40);
			loser.setBounds(550,300,450,40);

			add(winner);
			add(loser);

			if(!multiPlayer)///If It's not Multiplayer, set up a Timer that automoves the computer to the right cave.
			{
				compThink = new Timer(thinkTime,new ActionListener(){
					public void actionPerformed(ActionEvent evt)
					{
						int lowerBound;
						int upperBound;

						lowerBound = 550 + 12*rcorrectCave+134*(rcorrectCave-1)+20;
						upperBound = lowerBound + 50;

						boolean inRightCave;

						inRightCave = inBetween(rightXpos,lowerBound,upperBound);

						if(rightYpos >= 210 || inCave(rightXpos,false))
						{
							rightYpos-=2;
						}

						if(!inRightCave)
						{
							if (rcorrectCave == 1)
							{
								rightXpos-=1;
							}
							else if(rcorrectCave == 2)
							{}
							else if(rcorrectCave == 3)
								rightXpos+=1;
						}
						checkForCorrectAnswer();
						repaint();
						if(someoneHasWon())
						{
							showWinner();
						}
					}
				});

				compThink.start();
			}

			moveIcon = new Timer(1,new ActionListener(){
					public void actionPerformed(ActionEvent evt)
					{
						if(multiPlayer)
						{
							if(motion[4])
								rightYpos-=2;
							if(motion[5])
								rightYpos+=2;
							if(motion[6])
								rightXpos-=2;
							if(motion[7])
								rightXpos+=2;

							if(rightXpos >=960)
								rightXpos = 960;
							if(rightXpos <=550)
								rightXpos = 550;
							if(rightYpos <= 202)
								if(inCave(rightXpos,false))
								{	if(rightYpos<=118)
										rightYpos = 118;
									}
								else
									rightYpos = 202;
							if(rightYpos >= 508)
								rightYpos = 508;


						}

							if(motion[0])
									leftYpos-=2;
							if(motion[1])
									leftYpos+=2;
							if(motion[2])
									leftXpos-=2;
							if(motion[3])
									leftXpos+=2;

							if(leftXpos >=410)
								leftXpos = 410;
							if(leftXpos <=0)
								leftXpos = 0;
							if(leftYpos <= 202)
								if(inCave(leftXpos,true))
								{	if(leftYpos<=118)
										leftYpos = 118;
									}
								else
									leftYpos = 202;
							if(leftYpos >= 508)
								leftYpos = 508;


						checkForCorrectAnswer();
						repaint();
						if(someoneHasWon())
						{
							showWinner();
						}
					}
				});

			moveIcon.start();
        }

        public  int[] randomizeOrder(int[] arr, int start, int end)
		{
			for(int i = start; i < end+1; i++)
			{
				arr[i] = i;
			}

			Random rnd = new Random();

			for(int i = arr.length-1; i > 0; i--)
			{
				int index = rnd.nextInt(i+1);

				int a = arr[index];
				arr[index] = arr[i];
				arr[i] = a;
			}

			return arr;
		}

        public void makeQuestions(String mathLevel)				//Import the Questions fromt the Questions file
        {
			///Create a scanner for the cateogry of math selected and then use the scanner in order to fill the array of questions.

        	try									//Create a scanner for the Questions file
        	{
        		questionsReader = new Scanner(new File("assets/text/" + mathLevel +".txt"));
        	}
        	catch (FileNotFoundException er)
			{
				er.printStackTrace();
				System.exit(1);
			}

			String interstitialString;
			int questionNum = 0;
			int indexOfRowComma = 0;
			int oldIndex = 0;
			do
			{
				interstitialString = questionsReader.nextLine();

				indexOfRowComma = 0;
				oldIndex = 0;

				indexOfRowComma = interstitialString.indexOf(",");
				questions[questionNum][0] = interstitialString.substring(0,indexOfRowComma);
				oldIndex = indexOfRowComma;


				indexOfRowComma = interstitialString.indexOf(",",oldIndex+1);
				questions[questionNum][1] = interstitialString.substring(oldIndex+1,indexOfRowComma);
				oldIndex = indexOfRowComma;


				indexOfRowComma = interstitialString.indexOf(",",oldIndex+1);
				questions[questionNum][2] = interstitialString.substring(oldIndex+1,indexOfRowComma);
				oldIndex = indexOfRowComma;


				questions[questionNum][3] = interstitialString.substring(oldIndex+1);


				questionNum++;

			}
			while(questionsReader.hasNextLine()&&questionNum<questions.length);



        }

        public void paintComponent(Graphics g)
        {
			setBackground(floor);
			super.paintComponent(g);
			requestFocus();//requestFocus
			///FrameWork(Detailless) of the two Panels
			for(int i = 0; i <TRANSLATION_CONSTANT+1; i+=TRANSLATION_CONSTANT)
			{
				//Tunnel & PRoblem Box
				g.setColor(myGray);
				g.fillRect(0+i,0,450,118);
				g.setColor(Color.BLACK);
				g.drawRect(0+i,0,118,118);
				g.drawRect(118+i,0,332,118);

				for(int tunNum = 1; tunNum <= 3; tunNum++)
				{
					g.setColor(brown);
					g.fillRect(12*tunNum+134*(tunNum-1)+i,118,134,84);
					g.setColor(Color.BLACK);
					g.drawRect(12*tunNum+134*(tunNum-1)+i,118,134,84);
					g.fillArc(12*tunNum+134*(tunNum-1)+i,118,134,84*2,0,180);
				}

				g.setColor(myGray);
				g.fillRect(0+i,550,450,68);



			}
			///THe Hacks
				if(leftGameHighlight)
				{
					g.setColor(Color.GREEN);
					g.fillArc(12*lcorrectCave+134*(lcorrectCave-1),118,134,84*2,0,180);
					lcave[lcorrectCave-1].setForeground(Color.BLACK);
					g.setColor(myGray);
				}

				if(rightGameHighlight)
				{
					g.setColor(Color.GREEN);
					g.fillArc(12*rcorrectCave+134*(rcorrectCave-1)+TRANSLATION_CONSTANT,118,134,84*2,0,180);
					rcave[rcorrectCave-1].setForeground(Color.BLACK);
					g.setColor(myGray);
				}


			///Center Box
				g.fillRect(450,0,100,FRAME_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawRect(450,0,100,FRAME_HEIGHT);

				//Paint the Finish Line
				boolean black = true;
				for(int row = 0; row < 3; row++)
					for(int col = 0; col < 5; col++)
					{
						if(black)
							g.setColor(Color.BLACK);
						else
							g.setColor(Color.WHITE);

						g.fillRect(450+col*20,row*17,20,17);
						black = !black;
					}

				//Left Dot
				for(int i = 0; i < 3; i++)
				{
					g.setColor(new Color(leftBlue.getRed(),leftBlue.getGreen(),leftBlue.getBlue(),(3-i)*75));
					g.fillOval(473-i*3,550-i*3-25*leftTunnelsPassed,16+i*6,16+i*6);
				}
				g.setColor(Color.BLACK);
				g.drawOval(473,550-25*leftTunnelsPassed,16,16);
				g.setColor(leftBlue);
				g.drawLine(481,FRAME_HEIGHT,481,557-25*leftTunnelsPassed);


				//Right Dot
				for(int i = 0; i < 3; i++)
				{
					g.setColor(new Color(rightRed.getRed(),rightRed.getGreen(),rightRed.getBlue(),(3-i)*75));
					g.fillOval(511-i*3,550-i*3-25*rightTunnelsPassed,16+i*6,16+i*6);
				}
				g.setColor(Color.BLACK);
				g.drawOval(511,550-25*rightTunnelsPassed,16,16);
				g.setColor(rightRed);
				g.drawLine(519,FRAME_HEIGHT,519,557-25*rightTunnelsPassed);

			///Avatars & Game Problems
				g.drawImage(leftFace,leftXpos,leftYpos,AVATAR_SIZE,AVATAR_SIZE,this);
				g.drawImage(rightFace,rightXpos,rightYpos,AVATAR_SIZE,AVATAR_SIZE,this);

				//Left Tunnel Count
				g.setColor(Color.BLACK);
				g.setFont(new Font("",Font.BOLD,25));
				g.drawString("Tunnel",18,30);
				g.setFont(new Font("",Font.BOLD,70));
				if(!(leftTunnelsPassed <= 20))
				{
					if(leftTunnelsPassed < 11)
						g.drawString(""+(leftTunnelsPassed-1),40,100);
					else
						g.drawString(""+(leftTunnelsPassed-1),18,100);
				}
				else
				{
					if(leftTunnelsPassed < 11)
						g.drawString(""+(leftTunnelsPassed),40,100);
					else
						g.drawString(""+(leftTunnelsPassed),18,100);
				}

				//Right Tunnel Count
				g.setColor(Color.BLACK);
				g.setFont(new Font("",Font.BOLD,25));
				g.drawString("Tunnel",18+TRANSLATION_CONSTANT,30);
				g.setFont(new Font("",Font.BOLD,70));
				if(!(rightTunnelsPassed <= 20))
				{
					if(rightTunnelsPassed < 11)
						g.drawString(""+(rightTunnelsPassed-1),40 + TRANSLATION_CONSTANT,100);
					else
						g.drawString(""+(rightTunnelsPassed-1),18 + TRANSLATION_CONSTANT,100);
				}
				else
				{
					if(rightTunnelsPassed < 11)
						g.drawString(""+(rightTunnelsPassed),40 + TRANSLATION_CONSTANT,100);
					else
						g.drawString(""+(rightTunnelsPassed),18 + TRANSLATION_CONSTANT,100);
				}

				//Write the Problem in the Top Box
				g.setFont(new Font("",Font.BOLD,50));
				if(category.equals("Algebra 2"))
				{
					g.setFont(new Font("",Font.BOLD,30));
				}
				g.drawString(questions[leftQuestionOrder[leftQuestionsAnswered]][0],120,85);
				g.drawString(questions[rightQuestionOrder[rightQuestionsAnswered]][0],120+TRANSLATION_CONSTANT,85);



		}


		public boolean inCave(int xPos, boolean left)
		{
			boolean inCave = false;

			int low,high;
			int i = 1;
			while(i < 4 && !inCave)
			{
				low = 12*i+134*(i-1);
				high = 118 + low;

				if(left)
				{
					if(inBetween(xPos+2,low,high)&&inBetween(xPos+AVATAR_SIZE-2,low,high))
						inCave = true;
				}else
				{
					if(inBetween(xPos+2,TRANSLATION_CONSTANT+low,TRANSLATION_CONSTANT+high)&&inBetween(xPos+AVATAR_SIZE-2,TRANSLATION_CONSTANT+low,TRANSLATION_CONSTANT+high))
						inCave = true;
				}

				i++;
			}
			return inCave;
		}

		public boolean inBetween(int val, int lowBound, int upBound)
		{
			if(val > lowBound && val < upBound)
				return true;
			else
			{
				return false;
			}

		}
		public void keyPressed(KeyEvent evt)
		{
			int key = evt.getKeyCode();
    		if(canMove)
    		{
    		//Move in 10 pixel increments, handle exceptions to allow the Avatar to move into the caves
    		if(multiPlayer)
			{
				if(key == KeyEvent.VK_UP)
					motion[4] = true;
				else if(key == KeyEvent.VK_DOWN)
					motion[5] = true;
				else if(key == KeyEvent.VK_LEFT)
					motion[6] = true;
				else if(key == KeyEvent.VK_RIGHT)
					motion[7] = true;
			}

			if(key == KeyEvent.VK_W)
				motion[0] = true;
			else if(key == KeyEvent.VK_S)
				motion[1] = true;
			else if(key == KeyEvent.VK_A)
				motion[2] = true;
			else if(key == KeyEvent.VK_D)
				motion[3] = true;
			}
		}

        public void keyReleased(KeyEvent evt){
							//make Adjustments to avatar position and repaint
    		int key = evt.getKeyCode();

    		//Move in 10 pixel increments, handle exceptions to allow the Avatar to move into the caves
    		if(multiPlayer)
			{
				if(key == KeyEvent.VK_UP)
					motion[4] = false;
				else if(key == KeyEvent.VK_DOWN)
					motion[5] = false;
				else if(key == KeyEvent.VK_LEFT)
					motion[6] = false;
				else if(key == KeyEvent.VK_RIGHT)
					motion[7] = false;
			}
			if(key == KeyEvent.VK_W)
				motion[0] = false;
			else if(key == KeyEvent.VK_S)
				motion[1] = false;
			else if(key == KeyEvent.VK_A)
				motion[2] = false;
			else if(key == KeyEvent.VK_D)
				motion[3] = false;

		}

		public boolean someoneHasWon()
		{
			if(leftTunnelsPassed == 21 || rightTunnelsPassed == 21)
				return true;
			else
				return false;

		}

		public void checkForCorrectAnswer()
		{
			//check for solution (incorrect or correct)
    		if(leftYpos <190)
    		{


				if(inBetween(leftXpos,12*lcorrectCave+134*(lcorrectCave-1),12*lcorrectCave+134*(lcorrectCave-1)+134))
				{
					leftTunnelsPassed++;
				}else
				{
					if(leftTunnelsPassed != 1)
						leftTunnelsPassed--;
				}

				leftXpos = 185;
				leftYpos = 500;
				if(leftQuestionsAnswered == 99)
					leftQuestionsAnswered = 0;
				else
					leftQuestionsAnswered++;

				lcorrectCave = (int)(Math.random()*3)+1;
				lcave[lcorrectCave-1].setText(questions[leftQuestionOrder[leftQuestionsAnswered]][3]);
				for(int i = 0;i < 3; i++)
					lcave[i].setForeground(Color.WHITE);

				int startLoop = lcorrectCave;


				for(int i = 0; i < 2; i++)
				{
					lcave[(startLoop+i)%3].setText(questions[leftQuestionOrder[leftQuestionsAnswered]][i+1]);

				}


    		}

    		if(rightYpos < 190)
    		{
				if(inBetween(rightXpos,12*rcorrectCave+134*(rcorrectCave-1)+TRANSLATION_CONSTANT,12*rcorrectCave+134*(rcorrectCave-1)+134+TRANSLATION_CONSTANT))
				{
					rightTunnelsPassed++;
				}else
				{
					if(rightTunnelsPassed != 1)
						rightTunnelsPassed--;
				}

				rightXpos = 185+TRANSLATION_CONSTANT;
				rightYpos = 500;

				if(rightQuestionsAnswered == 99)
					rightQuestionsAnswered = 0;
				else
					rightQuestionsAnswered++;


				rcorrectCave = (int)(Math.random()*3)+1;

				rcave[rcorrectCave-1].setText(questions[rightQuestionOrder[rightQuestionsAnswered]][3]);
				for(int i = 0;i < 3; i++)
					lcave[i].setForeground(Color.WHITE);

				int startLoop = rcorrectCave;


				for(int i = 0; i < 2; i++)
				{
					rcave[(startLoop+i)%3].setText(questions[rightQuestionOrder[rightQuestionsAnswered]][i+1]);

				}


    		}

		}

    	public void keyTyped(KeyEvent evt)
    	{}

    	public void showWinner()
    	{

			///Disable the Key Movements && stop movement
			for(int i = 0; i < motion.length; i++)
				motion[i] = false;

			canMove = false;
			if(!multiPlayer)
			{
				compThink.stop();
			}

			moveIcon.stop();


			blinker = new Timer(750,new ActionListener(){
				public void actionPerformed(ActionEvent evt)
				{
					if(timeSpentFlashing < 6000)
					{

						endState = !endState;

						if(rightTunnelsPassed == 20)
						{
							winner.setBounds(550,300,450,40);
							loser.setBounds(0,300,450,40);
						}else if(leftTunnelsPassed == 20)
						{
							winner.setBounds(0,300,450,40);
							loser.setBounds(550,300,450,40);
						}

						winner.setVisible(endState);
						loser.setVisible(endState);

						timeSpentFlashing+=750;
					}else
					{
						blinker.stop();
						mainCards.show(main,"Start");
						main.remove(gamePlay);

						///UpdateIfLevelBeaten or Not
						if(category.equals("Arithmetic"))
							levelBeat[0] = true;
						if(category.equals("Algebra 1"))
							levelBeat[1] = true;
						if(category.equals("Algebra 2"))
							levelBeat[2] = true;
					}

				}
			});

			blinker.start();


		}





		}

}
