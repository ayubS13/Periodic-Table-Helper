/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ayubs
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.*;
import java.awt.Font;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class crosswordPage extends javax.swing.JFrame{
    
    public static class WordGrid{
        char[][] gridValues = new char[20][20]; //the standard puzzle will be 20 by 20 letters
        ArrayList<String> solutionsListWithCoordinates = new ArrayList<String>(); //this is the solutionsListWithCoordinates list and should contain all 10 random generated words
        ArrayList<String> solutionElementNames = new ArrayList<String>();
        
        public boolean contains(ArrayList<Element> elements){ //checks if the solutionsListWithCoordinates list has all the words from the list generated
            for(int i = 0; i< elements.size(); i++){
                if(solutionsListWithCoordinates.contains(elements.get(i).getName()) == false){
                    return false;
                }
            }
            return true;
        }
        
        public boolean holds(String word){ //checks if the solutionsListWithCoordinates list already has the element name passed in
            return solutionElementNames.contains(word);
        }
        
        public String getCoordinates(String word){
           for(int i = 0; i< solutionsListWithCoordinates.size(); i++){
               String elementname = solutionsListWithCoordinates.get(i).substring(0,solutionsListWithCoordinates.get(i).indexOf(" "));
               String coordinates = solutionsListWithCoordinates.get(i).substring(solutionsListWithCoordinates.get(i).indexOf(" "));
               if(elementname.equals(word.toUpperCase())){
                   return elementname+coordinates;
               }
           }
           return "";
        }
    }
    /**
     * Creates new form crosswordPage
     */
    
    
    ArrayList<Element> allElements = new ArrayList<Element>();
    ArrayList<Element> randomElements = new ArrayList<Element>();
    ArrayList<Element> foundElements = new ArrayList<Element>();
    int initialRow=0, initialColumn = 0, totalPoints = 0, wordsToFind = 30;
    Point initialPoint, endPoint;
    boolean mouseExited = false, printed = false, isDrawable = true;
    LinesListClass linesList;
    String direction= "";
    Rectangle currentCell, previousCell;
    WordGrid generated;
    int gridSize = 20*20;
    Random r = new Random();
    int[][]directions={{1,0},{0,1},{1,1},{1,-1},{-1,0},{0,-1},{-1,1},{-1,-1}};
    HashMap<Integer, ArrayList<Integer>> myMap; //this will store the found words on the jTable grid;
    
    public crosswordPage(List allElements) {
        prepareBackEnd(allElements);
        prepareFrontEnd();
    }
    
    public void prepareBackEnd(List allElements){ //functions that run in the background
        //initializing HashMap
        myMap = new HashMap<Integer, ArrayList<Integer>>();
        for(int i = 0; i< 20; i++){
            myMap.put(i, new ArrayList<Integer>());
        }
        linesList = new LinesListClass();
        convertListToArrayList(allElements); //convert the elements from a linked list to an arraylist
        generateRandomElements(); //select 20 random elements
        generated = createGrid();
    }
    
    public void prepareFrontEnd(){ //what the user sees
        initComponents();
        this.setLocationRelativeTo(null);
        this.setSize(900,500);
        generateGraphics(); //print the screen
        displayMissingOnScreen(); //display these elements to be found
        updateJTable(generated);
    }
    
    public void convertListToArrayList(List elements){
        Element curr = elements.head;
        while(curr != null){
            allElements.add(curr);
            curr = curr.next;
        }
    }
    
    public void generateRandomElements(){
        randomElements.clear();
        int elementSize = allElements.size();
        Random rand = new Random();
        boolean unique = true;
        int randomIndex;
        Element elt;
        
        while(randomElements.size() != wordsToFind){
            do{
                unique = true;
                randomIndex = rand.nextInt(elementSize); //this will return a number between 0 and allElements.size()-1
                elt = allElements.get(randomIndex);
                if(randomElements.contains(elt)){
                    unique = false;
                }
            }while(unique == false);
            randomElements.add(elt);
        }
        System.out.println("There are " + randomElements.size() + " random elements generated" );
        for(Element elementGen : randomElements){
            System.out.println(elementGen.getName());
        }
    }
    
    
    public void updateJTable(WordGrid temp){
        for(int i = 0; i < 20; i++){
            for(int j = 0; j< 20; j++){
                jTable1.setValueAt(temp.gridValues[i][j], i, j);
            }
        }
        
        for(int i = 0; i < temp.gridValues.length; i++){
            for(int j = 0; j< temp.gridValues[0].length; j++){
                char c = temp.gridValues[i][j];
                System.out.print( c + "  ");
            }
            System.out.println();
        }
        System.out.println(temp.solutionsListWithCoordinates.size());
        for(int i = 0 ;i< temp.solutionsListWithCoordinates.size(); i++){
            System.out.println(temp.solutionsListWithCoordinates.get(i));
        }
        System.out.println(temp.solutionsListWithCoordinates.size());
    }
    
    
    public WordGrid createGrid(){
        WordGrid temp;;
        do{
            temp = new WordGrid();
            do{
                for(int i = 0 ; i< randomElements.size(); i++){
                    System.out.println("inner for loop");
                    placeWordInGrid(temp, randomElements.get(i).getName().toUpperCase());
                    if(temp.solutionsListWithCoordinates.contains(randomElements)){  
                        return temp; //this is a successfully genearted Grid of words with everything filled and all words
                    }
                }
                System.out.println("inner do while loop");
                if(temp.solutionsListWithCoordinates.size() != randomElements.size())
                    break;
            }while(temp.solutionsListWithCoordinates.size() != randomElements.size());
            System.out.println("outer do while loop");
        }while(temp.solutionsListWithCoordinates.size() != wordsToFind);
        
        for(int i = 0;i< temp.gridValues.length; i++){
            for(int j = 0; j< temp.gridValues[0].length; j++){
                if(temp.gridValues[i][j]==0)
                    temp.gridValues[i][j] = (char)(r.nextInt(26)+'A');
            }
        }
        return temp;
    }

    //The algorithm to generate the word grid has been adopted from Sylvain Saurel
    //https://www.youtube.com/watch?v=mxKIZgx3jIY -- author Sylvain Saurel
    //it is slightly modified but is largely his
    public int placeWordInGrid(WordGrid temp, String word){
        int randomDirection = r.nextInt(directions.length);
        int randomPosition = r.nextInt(gridSize);
        for(int dir = 0; dir< directions.length; dir++){
            dir = (dir+randomDirection)%directions.length;
            for(int pos = 0; pos< gridSize; pos++){
                pos = (pos+randomPosition)%gridSize;
                int lettersPlaced = tryLocation(temp, word, dir,pos/20,pos%20);
                if(lettersPlaced > 0){
                    return lettersPlaced;
                }
            }
        }
        return 0;
    }
    
    //The algorithm to generate the word grid has been adopted from Sylvain Saurel
    //https://www.youtube.com/watch?v=mxKIZgx3jIY -- author Sylvain Saurel
    //it is slightly modified but is largely his
    public int tryLocation(WordGrid temp, String word, int direction, int row, int column){
        //illegal directions
        if((directions[direction][0]==1 && (word.length()+column) > 20)
           || (directions[direction][0]==-1 && (word.length()-1)>column)   
           || (directions[direction][1] ==1 && (word.length()+row) >20)
           || directions[direction][1] == -1 && (word.length()-1)>row){
                return 0;
        }
        
        int r,c,i,overlaps = 0;
        //check if the name being inserted overlaps anything, if it doesnt then save the row and column
        for( i = 0, r = row, c = column; i< word.length(); i++ ){
            if(temp.gridValues[r][c] != 0 && temp.gridValues[r][c] != word.charAt(i)){
                return 0;
            }
            c += directions[direction][0];
            r += directions[direction][1];
        }
        
        //now that we have the begining row and column where the word doesnt over lap, we can go ahead and do this
        for(i = 0, r=row, c = column; i< word.length(); i++){
            if(temp.gridValues[r][c] == word.charAt(i)){
                overlaps++;
            }else{
                temp.gridValues[r][c] = word.charAt(i);
            }
            if(i < word.length()-1){
                c += directions[direction][0];
                r += directions[direction][1];
            }
        }
        
        int lettersPlaced = word.length()-overlaps;
        if(lettersPlaced>0){
            temp.solutionElementNames.add(word.toUpperCase());                                  //word, column+1,20-row, c+1,20-r
            if(c > column && r > row){
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r,"southeast"));
            }if(c > column && r== row){
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r, "east"));
            }if(c > column && r < row){
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r, "northeast"));
            }if(c < column && r >row){
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r, "southwest"));
            }if(c < column && r == row){
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r, "west"));
            }if(c < column && r < row){
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r, "northwest"));
            }if(c == column && r > row){//south
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r, "south"));
            }if(c == column && r < row){
                temp.solutionsListWithCoordinates.add(String.format("%-10s (%d,%d)(%d,%d) %S", word, column,row, c,r, "north"));
            }
            
        }
        return lettersPlaced;
    }
    
    public void displayMissingOnScreen(){
        DefaultListModel mk = new DefaultListModel();
        for(int i = 0 ;i< randomElements.size(); i++){
            mk.addElement(randomElements.get(i).getName());
        }
        missingList.setModel(mk);
    }
    
    public void displayFoundOnScreen(){
        DefaultListModel md = new DefaultListModel();
        for(int i = 0; i< foundElements.size(); i++){
            md.addElement(foundElements.get(i).getName());
        }
        foundList.setModel(md);
    }
    
    public void generateGraphics(){
        //missingList.setOpaque(false);
        scoreTextField.setBackground(new java.awt.Color(0,0,0,1));
        scoreTextField.setBorder(null);
                        
        mainMenuButton.setBorder(null);
        mainMenuButton.setOpaque(false);
        mainMenuButton.setContentAreaFilled(false); 
        
        recreateButton.setBorder(null);
        recreateButton.setOpaque(false);
        recreateButton.setContentAreaFilled(false); 
        
        solveButton.setBorder(null);
        solveButton.setOpaque(false);
        solveButton.setContentAreaFilled(false); 
        
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        DefaultListCellRenderer renderer2 = new DefaultListCellRenderer();
        renderer.setOpaque( false );
        
        missingList.setCellRenderer( renderer );
        foundList.setCellRenderer(renderer2);
        
        missingList.setCellRenderer(new TransparentListCellRenderer());
        missingList.setOpaque(false);
        missingList.setSelectionBackground(new java.awt.Color(211,211,211,64)); 
        
        foundList.setCellRenderer(new TransparentListCellRenderer());
        foundList.setOpaque(false);
        foundList.setSelectionBackground(new java.awt.Color(211,211,211,64));
        
        jScrollPane4.setOpaque(false);
        jScrollPane4.getViewport().setOpaque(false);
        
        jScrollPane2.setOpaque(false);
        jScrollPane2.getViewport().setOpaque(false);
        
        recreateButton.setOpaque(false);
        recreateButton.setContentAreaFilled(false); //to make the content area transparent
        
        solveButton.setOpaque(false);
        solveButton.setContentAreaFilled(false); //to make the content area transparent
        
        mainMenuButton.setOpaque(false);
        mainMenuButton.setContentAreaFilled(false); //to make the content area transparent

        wordSelected.setEditable(false);
        wordSelected.setBackground(new java.awt.Color(0,0,0,64));
        
        //some more graphics stuff to make the grid transparent
        jScrollPane1.getColumnHeader().setVisible(false); //jScrollPane1 is the jTable
        jTable1.setOpaque(false);
        jTable1.setFillsViewportHeight(true);
        jTable1.setBackground(new java.awt.Color(255,255,208,15));
        jTable1.setOpaque(false);
        ((DefaultTableCellRenderer)jTable1.getDefaultRenderer(Object.class)).setOpaque(false);
        ((DefaultTableCellRenderer)jTable1.getDefaultRenderer(Object.class)).setHorizontalAlignment( JLabel.CENTER );
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);
        jTable1.setDefaultEditor(Object.class, null);
        
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        foundList = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        scoreTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        wordSelected = new javax.swing.JTextField();
        mainMenuButton = new javax.swing.JButton();
        recreateButton = new javax.swing.JButton();
        solveButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        missingList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Word Search");
        setMinimumSize(getPreferredSize());
        setResizable(false);
        setSize(getPreferredSize());
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Palatino Linotype", 1, 48)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Periodic Table Made Easy");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 580, 70));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTable1.setBackground(new java.awt.Color(102, 102, 102));
        jTable1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10", "Title 11", "Title 12", "Title 13", "Title 14", "Title 15", "Title 16", "Title 17", "Title 18", "Title 19", "Title 20"
            }
        ));
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTable1.setDropMode(javax.swing.DropMode.ON);
        jTable1.setEditingColumn(0);
        jTable1.setEditingRow(0);
        jTable1.setFillsViewportHeight(true);
        jTable1.setFocusTraversalPolicyProvider(true);
        jTable1.setFocusable(false);
        jTable1.setMaximumSize(getPreferredSize());
        jTable1.setMinimumSize(getPreferredSize());
        jTable1.setPreferredSize(new java.awt.Dimension(430, 330));
        jTable1.setRowSelectionAllowed(false);
        jTable1.setSelectionBackground(new java.awt.Color(211, 211, 211));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jTable1.setShowGrid(false);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                getSelectedAreaMouseReleased(evt);
                mouseDraggedWithSelection(evt);
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseEnteredJTable(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseExitedJTable(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                getPointAtMousePressed(evt);
                getSelectedAreaMouseReleased(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mouseReleasedCheckWord(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 430, 330));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        foundList.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jScrollPane2.setViewportView(foundList);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 160, 170, 250));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Found");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 130, 120, 30));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Score:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 420, 80, 30));

        scoreTextField.setEditable(false);
        scoreTextField.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        scoreTextField.setForeground(new java.awt.Color(255, 255, 255));
        scoreTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scoreTextFieldActionPerformed(evt);
            }
        });
        getContentPane().add(scoreTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 420, 100, 30));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Missing");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 130, 120, 30));

        wordSelected.setEditable(false);
        wordSelected.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        wordSelected.setForeground(new java.awt.Color(255, 255, 255));
        wordSelected.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        wordSelected.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getContentPane().add(wordSelected, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 80, 330, 40));

        mainMenuButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        mainMenuButton.setForeground(new java.awt.Color(255, 255, 255));
        mainMenuButton.setText("Main Menu");
        mainMenuButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        mainMenuButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mainMenuButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        mainMenuButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseEnteredButtonChangeColor(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseExitButton(evt);
            }
        });
        mainMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainMenuButtonActionPerformed(evt);
            }
        });
        getContentPane().add(mainMenuButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 420, -1, 30));

        recreateButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        recreateButton.setForeground(new java.awt.Color(255, 255, 255));
        recreateButton.setText("Recreate Puzzle");
        recreateButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        recreateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        recreateButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        recreateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseEnteredButtonChangeColor(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseExitButton(evt);
            }
        });
        recreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recreateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(recreateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 420, -1, 30));

        solveButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        solveButton.setForeground(new java.awt.Color(255, 255, 255));
        solveButton.setText("Solve Puzzle");
        solveButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        solveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        solveButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        solveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseEnteredButtonChangeColor(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mouseExitButton(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                solveButtonPressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                solveButtonReleased(evt);
            }
        });
        getContentPane().add(solveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 420, -1, 30));

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        missingList.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jScrollPane4.setViewportView(missingList);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, 170, 250));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/backgroundImage.jpg"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 500));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scoreTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scoreTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_scoreTextFieldActionPerformed

    
    private void getSelectedAreaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_getSelectedAreaMouseReleased
        if(isDrawable == true){
            String word = "";
            if(mouseExited == false ){
                endPoint = evt.getPoint();
                int endRow= jTable1.rowAtPoint(endPoint);
                int endColumn = jTable1.columnAtPoint(endPoint);

                char[][] areaSelected = new char [jTable1.getSelectedRows().length][jTable1.getSelectedColumns().length]; //this is the selected area      
                int [] selectedRows = jTable1.getSelectedRows();
                int [] selectedColumns = jTable1.getSelectedColumns();

                TableModel temp = jTable1.getModel(); //this is all the data of the model
                int rows = 0; //indexes used to fill up the area selected
                int cols = 0; //indexes used to fill up the area selected
                for(int i = 0; i < temp.getRowCount(); i++){
                    if(i >= selectedRows[0] && i<= selectedRows[selectedRows.length-1]){
                        for(int j = 0 ; j< temp.getColumnCount(); j++){
                            if(j >= selectedColumns[0] && j <= selectedColumns[selectedColumns.length-1]){
                                areaSelected[rows][cols] = temp.getValueAt(i,j).toString().charAt(0);
                                cols++;

                            }     
                        }
                        cols=0;
                       rows++;
                    }
                }

                if(endColumn != -1 && endRow != -1){ 
                    if(areaSelected.length ==1 && areaSelected[0].length ==1){
                        word = (String)jTable1.getValueAt(initialRow, initialColumn).toString();
                        drawLine(endPoint, "single");
                    }
                    if(areaSelected.length == areaSelected[0].length ){ //in the case of a diagonal, the length of the diagonal will be the same length as the side length
                        if(initialRow > endRow && initialColumn > endColumn){ //this is north west
                            word = getWordInGrid(areaSelected.length-1,areaSelected[0].length-1,7,areaSelected[0].length, areaSelected);
                            direction = "northwest";
                            drawLine(endPoint, "diagonal");
                        }
                        else if(initialRow < endRow && initialColumn > endColumn){//south west

                            word= getWordInGrid(0,areaSelected[0].length-1,5,areaSelected[0].length, areaSelected);
                            direction = "southwest";
                            drawLine(endPoint, "diagonal");
                        }
                        else if(initialRow > endRow && initialColumn < endColumn){
                            word = getWordInGrid(areaSelected.length-1,0,1,areaSelected[0].length, areaSelected); //going north east
                            direction = "northeast";
                            drawLine(endPoint, "diagonal");
                        }
                        else if(initialRow < endRow && initialColumn < endColumn){
                            word = getWordInGrid(0,0,3,areaSelected[0].length, areaSelected);//going southeast
                            direction = "southeast";
                            drawLine(endPoint, "diagonal");
                        }
                    }

                    //a vertical selection of words
                    if(areaSelected.length >=0 && areaSelected[0].length ==1){
                        if(initialColumn == endColumn && initialRow < endRow){
                            word = getWordInGrid(0,0,4,areaSelected.length, areaSelected);//4 is a downward direction
                            drawLine(endPoint, "vertical");
                            direction = "south";
                        }
                        if(initialColumn == endColumn && initialRow > endRow){
                            word = getWordInGrid(areaSelected.length-1,0,0,areaSelected.length,areaSelected);//start row, start col, direction, length, int[][]
                            drawLine(endPoint, "vertical");
                            direction = "north";
                        }
                    }

                    //a horizontal selection of words
                    if(areaSelected.length == 1 && areaSelected[0].length >=0){//if we know we are horizontal, all we need to know is left or right
                        if(initialRow == endRow && initialColumn < endColumn){
                            word = getWordInGrid(0,0,2,areaSelected[0].length, areaSelected);
                            direction = "east";
                            drawLine(endPoint, "horizontal");
                        }
                        if(initialRow == endRow && initialColumn > endColumn){
                            word = getWordInGrid(0,areaSelected[0].length-1,6,areaSelected[0].length,areaSelected);//start row, start col, direction, length, int[][]
                            direction = "west";
                            drawLine(endPoint, "horizontal");
                        }
                    }

                    wordSelected.setText(word);
                }
            }
        }
    }//GEN-LAST:event_getSelectedAreaMouseReleased
    public void drawLine(Point secondPoint, String type ){
        Graphics g ;
        g = jTable1.getGraphics();
        
        g.setColor(Color.RED);
        Graphics2D g2 = (Graphics2D) g;
        Stroke stroke3 = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2.setStroke(stroke3);
        if(mouseExited == false){
            Rectangle second = null;
            Rectangle first = jTable1.getCellRect(initialRow, initialColumn, true); //this is the cell where the mouse was pressed
            //System.out.println("The end of the line is "+ jTable1.getValueAt(endRow, endColumn)+ endRow + "," + endColumn + " Coordinates:"+ secondPoint.x +", " +secondPoint.y );
            second = jTable1.getCellRect(jTable1.rowAtPoint(endPoint),jTable1.columnAtPoint(endPoint),true);
            if(second != null && jTable1.rowAtPoint(endPoint) != -1 && jTable1.columnAtPoint(endPoint)!= -1){
                if(type.equals("horizontal")&& mouseExited == false){
                    g.drawLine((first.x+first.width/2), (first.y+first.height/2), (second.x+second.width/2), (second.y+second.height/2));
                }else if(type.equals("vertical") && mouseExited == false ){
                    g.drawLine((first.x+first.width/2), (first.y+first.height/2), (second.x+second.width/2), (second.y+second.height/2));
                }else if(type.equals("diagonal") && mouseExited == false){
                    g.drawLine((first.x+first.width/2), (first.y+first.height/2), (second.x+second.width/2), (second.y+second.height/2));
                }
            }
        }
    }

    public boolean isValidLine(String word){ //checks if the word created by the line is part of the words needed to be found
        for(int i = 0; i< randomElements.size(); i++){
            if(randomElements.get(i).getName().toUpperCase().equals(word)){
                if(generated.holds(word) == true){
                    return true; //this is a valid line because the grid holds this word as a solution
                }
            }
        }
        return false;
    }

    //this is very important, it gives me the initial point/cell where the mouse was clicked
    private void getPointAtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_getPointAtMousePressed
        if(isDrawable == true){
            initialPoint = evt.getPoint();
            endPoint = evt.getPoint();
            initialRow = jTable1.rowAtPoint(initialPoint);
            initialColumn = jTable1.columnAtPoint(initialPoint);
        }
    }//GEN-LAST:event_getPointAtMousePressed

    private void mouseReleasedCheckWord(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseReleasedCheckWord
        // TODO add your handling code here:
        if(isDrawable == true){
            currentCell = previousCell = null; //reset the cell selection
            Rectangle second = null;
            Rectangle first = jTable1.getCellRect(initialRow, initialColumn, true); //this is the cell where the mouse was pressed
            if(mouseExited == false){
                second = jTable1.getCellRect(jTable1.rowAtPoint(endPoint),jTable1.columnAtPoint(endPoint),true);
            }

            if(isValidLine(wordSelected.getText().toUpperCase())){
                int firstx=0, firsty=0, secondx=0, secondy = 0;
                //listOfLines.addLine(first.x+first.width/2, first.y+first.height/2,(second.width/2+second.x), (second.height/2+second.y), Color.yellow);
                //here we will change the colors and backgrounds of the jTable
                for(int i = 0; i< randomElements.size(); i++){
                    if(randomElements.get(i).getName().toUpperCase().equals(wordSelected.getText())){
                        String name = generated.getCoordinates(wordSelected.getText()).replaceAll(" ", "").substring(0, generated.getCoordinates(wordSelected.getText()).replaceAll(" ", "").indexOf('(') );
                        String coordinates = generated.getCoordinates(wordSelected.getText()).replaceAll(" ", "");
                        firstx = Integer.parseInt(coordinates.substring(coordinates.indexOf('(')+1,coordinates.indexOf(',')));
                        firsty = Integer.parseInt(coordinates.substring(coordinates.indexOf(',')+1, coordinates.indexOf(")")));
                        String otherHalf = coordinates.substring(coordinates.indexOf('(',coordinates.indexOf('(')+1));
                        secondx = Integer.parseInt(otherHalf.substring(otherHalf.indexOf('(')+1,otherHalf.indexOf(',')));
                        secondy = Integer.parseInt(otherHalf.substring(otherHalf.indexOf(',')+1, otherHalf.indexOf(")")));
                        System.out.println(firstx +" " + firsty + " " + secondx+ " " +secondy);
                        highlightWordOnTable(firstx, firsty, secondx, secondy, name);
                        totalPoints+=100;
                        System.out.println(totalPoints);
                        scoreTextField.setText(""+totalPoints);
                        for(int j = 0 ; j < randomElements.size(); j++){
                            if(randomElements.get(j).getName().toUpperCase().equals(name.toUpperCase())){
                                foundElements.add(randomElements.get(j));
                                randomElements.remove(j);
                                displayFoundOnScreen();
                                displayMissingOnScreen();
                            }
                        }
                    }
                }
            }
            generateGraphics();
        }
    }//GEN-LAST:event_mouseReleasedCheckWord

    public void highlightWordOnTable(int column, int row, int secondc, int secondr, String word){    
        int r = row, c = column;
        
        // iterate once for each character in the output
        for (int i = 0; i < word.length(); i++) {
            switch (direction.toUpperCase()) { // assumes grid[0][0] is in the upper-left
                
                case "NORTH": //columns behave differntly in jTable so have to do something extra
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r,myMap.get(c)));
                    r--;
                    break; // north
                case "NORTHEAST":
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r,myMap.get(c)));
                    r--;
                    c++;
                    break; // north-east
                case "EAST":
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r,myMap.get(c)));
                    c++;
                    break; // east
                case "SOUTHEAST":
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r,myMap.get(c)));
                    r++;
                    c++;
                    break; // south-east
                case "SOUTH": //columns behave differntly in jTable so have to do something extra
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r,myMap.get(c)));
                    r++;
                    break; // south
                case "SOUTHWEST": //this is breaking down when I do solve puzzle
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r,myMap.get(c)));
                    r++;
                    c--;
                    break; // south-west*/
                case "WEST":
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r,myMap.get(c)));
                    c--;
                    break; // west
                case "NORTHWEST":
                    jTable1.getColumnModel().getColumn(c).setCellRenderer(new changeCellColor(r, myMap.get(c)));
                    r--;
                    c--;
                    break; // north-west
            }
        }
    }
    private void mouseDraggedWithSelection(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseDraggedWithSelection
        // TODO add your handling code here:
        if(isDrawable == true){
            endPoint = evt.getPoint();
            if(jTable1.contains(endPoint)){
                mouseExited = false;
            }
        }
    }//GEN-LAST:event_mouseDraggedWithSelection

    private void mouseExitedJTable(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseExitedJTable
        if((evt.getPoint().y > jTable1.getY()+jTable1.getHeight() && (evt.getPoint().x < jTable1.getX()+jTable1.getWidth() && evt.getPoint().x > jTable1.getX()))){
            mouseExited = true;
        }
        wordSelected.setText("");
    }//GEN-LAST:event_mouseExitedJTable

    private void mouseEnteredJTable(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseEnteredJTable
        if(jTable1.contains(evt.getPoint()))
            mouseExited = false;
    }//GEN-LAST:event_mouseEnteredJTable

    private void recreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recreateButtonActionPerformed
        // TODO add your handling code here:
        jTable1.setFocusable(true);
        jTable1.setEnabled(true);
        linesList = null;
        totalPoints = 0;
        scoreTextField.setText(""+totalPoints);
        linesList = new LinesListClass();
        printed = false;
        isDrawable = true;
        myMap.clear();
        myMap = new HashMap<Integer, ArrayList<Integer>>();
        for(int i = 0; i< 20; i++){//these are the 20 columns
            myMap.put(i, new ArrayList<Integer>());
        }
        //removing the coloring from the grid
        for(int i = 0; i< 20; i++){
            for(int j =0 ; j< 20; j++){  //this is an overloaded constructor, the last value true for reseting colors of the table
                jTable1.getColumnModel().getColumn(i).setCellRenderer(new changeCellColor( true));
            }
        }
        randomElements.clear();
        foundElements.clear();
        
        generateRandomElements(); //select 20 random elements
        generated = createGrid();
        generateGraphics(); //print the screen
        displayMissingOnScreen(); //this prints the elements that are yet to be found
        displayFoundOnScreen();//this prints the elements that are already found
        updateJTable(generated);
    }//GEN-LAST:event_recreateButtonActionPerformed

    private void mainMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainMenuButtonActionPerformed
        // TODO add your handling code here:
        StartPage stpg = new StartPage();
        this.setVisible(false);
        stpg.setVisible(true);
    }//GEN-LAST:event_mainMenuButtonActionPerformed

    private void mouseEnteredButtonChangeColor(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseEnteredButtonChangeColor
        // TODO add your handling code here:
        JButton imDone = null;
        if(evt.getSource() instanceof JButton){
            imDone =(JButton)evt.getSource(); 
        }
        if(imDone != null){
            if(evt.getSource() == mainMenuButton){
                mainMenuButton.setForeground(Color.GRAY);
            }else if(evt.getSource() == solveButton){
                solveButton.setForeground(Color.GRAY);
            }else if(evt.getSource() == recreateButton){
                recreateButton.setForeground(Color.GRAY);
            }
        }
    }//GEN-LAST:event_mouseEnteredButtonChangeColor

    private void mouseExitButton(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseExitButton
        // TODO add your handling code here:
        mainMenuButton.setForeground(Color.WHITE);
        recreateButton.setForeground(Color.WHITE);
        solveButton.setForeground(Color.WHITE);
    }//GEN-LAST:event_mouseExitButton

    private void solveButtonReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_solveButtonReleased
        // TODO add your handling code here:
        if(printed == false){
            printed = true;
            Graphics g ;
            g = jTable1.getGraphics();

            g.setColor(Color.RED);
            Graphics2D g2 = (Graphics2D) g;
            Stroke stroke3 = new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
            g2.setStroke(stroke3);
            linesList.printLines(g);
        }
    }//GEN-LAST:event_solveButtonReleased

    private void solveButtonPressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_solveButtonPressed
        // TODO add your handling code here:
        //we just have to loop through the solutions list in the grid class
        //then put those solutions on screen. Simple
        solveButton.setForeground(Color.WHITE);
        isDrawable = false;
        jTable1.setFocusable(false);
        jTable1.setEnabled(false);

        if(printed == false){
            int firstx = 0, firsty = 0, secondx = 0, secondy = 0;

            for(String temp: generated.solutionsListWithCoordinates){

                String name = temp.substring(0, temp.indexOf('(')).replaceAll(" ", "");
                    firstx = Integer.parseInt(temp.substring(temp.indexOf('(')+1, temp.indexOf(','))); //x is columns
                        firsty = Integer.parseInt(temp.substring(temp.indexOf(',')+1, temp.indexOf(')')));// y is the rows
                    String secondHalf = temp.substring(temp.indexOf('(', temp.indexOf('(')+1));
                        secondx = Integer.parseInt(secondHalf.substring(secondHalf.indexOf('(')+1, secondHalf.indexOf(',')));
                            secondy = Integer.parseInt(secondHalf.substring(secondHalf.indexOf(',')+1, secondHalf.indexOf(')')));
                        direction = secondHalf.substring(secondHalf.indexOf(')')+1).replaceAll(" ","");
                    highlightWordOnTable(firstx, firsty, secondx, secondy, name); //the last thing passed in is direction;

                    //these are the cell locations
                    Rectangle firstRect = jTable1.getCellRect(firsty, firstx,true);
                    Rectangle secondRect = jTable1.getCellRect(secondy, secondx,true);

                    linesList.addLine((firstRect.x + firstRect.width/2),
                        (firstRect.y+firstRect.height/2),
                        (secondRect.x+secondRect.width/2),
                        (secondRect.y +secondRect.height/2),
                        new java.awt.Color(126,126,126,60));

                    for(int j = 0 ; j < randomElements.size(); j++){
                        if(randomElements.get(j).getName().toUpperCase().equals(name.toUpperCase())){
                            foundElements.add(randomElements.get(j));
                            randomElements.remove(j);
                            displayFoundOnScreen();
                            displayMissingOnScreen();
                            generateGraphics();

                        }
                    }
                }
            }
    }//GEN-LAST:event_solveButtonPressed


    //this method is heavily inspired by the "Speedy Hash Table Lab from CS-2150"
    //The original author of this code, specifically the switch case to get the word, is not me
    //this method has been modified to better fit my needs
public String getWordInGrid (int startRow, int startCol, int direction, int length, char[][]areaSelected) {
   
    String output = "";
    int r = startRow, c = startCol;
    // iterate once for each character in the output
    for (int i = 0; i < length; i++) {
        // if the current row or column is out of bounds, then break
        if (c >= areaSelected[0].length|| r >= areaSelected.length || r < 0 || c < 0) {
            break;
        }
        output += areaSelected[r][c];
        switch (direction) { // assumes grid[0][0] is in the upper-left
            case 0:
                r--;
                break; // north
            case 1:
                r--;
                c++;
                break; // north-east
            case 2:
                c++;
                break; // east
            case 3:
                r++;
                c++;
                break; // south-east
            case 4:
                r++;
                break; // south
            case 5:
                r++;
                c--;
                break; // south-west*/
            case 6:
                c--;
                break; // west
            case 7:
                r--;
                c--;
                break; // north-west
        }
    }
    return output;
}
    
    public class TransparentListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setForeground(Color.WHITE);
            setOpaque(isSelected);
            this.setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
    
    public class changeCellColor extends DefaultTableCellRenderer{
        int rowChange;
        ArrayList<Integer> cc;
        boolean reset;
        public changeCellColor(int temp, ArrayList<Integer> currentC){
            rowChange = temp;
            cc = currentC;
            updateColumn();
        }
        
        public changeCellColor( boolean reset){
            this.reset = reset;
        }
        private void updateColumn(){
            if(cc != null){
                if(!cc.contains(rowChange)){
                    cc.add(rowChange);
                }
            }
        }
       
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if(reset == false){
                for(int i = 0; i< cc.size(); i++){
                    if(row == cc.get(i)){
                        renderer.setForeground(Color.RED); //if matches set to red
                        //renderer.setBackground(new java.awt.Color(126,126,126,100));
                        renderer.setFont(renderer.getFont().deriveFont(Font.BOLD));
                    }
                }

                if(!cc.contains(row)){
                    //renderer.setBackground(new java.awt.Color(0,0,0,1));
                    renderer.setForeground(Color.WHITE);
                }
            }else{
                //renderer.setForeground(Color.WHITE); //if matches set to red
                renderer.setBackground(new java.awt.Color(0,0,0,1));
            }
            return renderer;
        }
    }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(crosswordPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(crosswordPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(crosswordPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(crosswordPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new crosswordPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> foundList;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton mainMenuButton;
    private javax.swing.JList<String> missingList;
    private javax.swing.JButton recreateButton;
    private javax.swing.JTextField scoreTextField;
    private javax.swing.JButton solveButton;
    private javax.swing.JTextField wordSelected;
    // End of variables declaration//GEN-END:variables
}
