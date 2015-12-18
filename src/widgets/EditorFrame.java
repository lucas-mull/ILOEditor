package widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import figures.Drawing;
import figures.creationListeners.AbstractCreationListener;
import figures.enums.FigureType;
import figures.enums.LineType;
import figures.enums.PaintToType;
import filters.EdgeColorFilter;
import filters.FillColorFilter;
import filters.LineFilter;
import filters.ShapeFilter;
import utils.IconFactory;
import utils.PaintFactory;

/**
 * Classe de la fenêtre principale de l'éditeur de figures
 * @author davidroussel
 */
public class EditorFrame extends JFrame
{
	/**
	 * Le nom de l'éditeur
	 */
	protected static final String EditorName = "Figure Editor v3.0";
	
	/**
	 * Le modèle de dessin sous-jacent;
	 */
	protected Drawing drawingModel;

	/**
	 * La zone de dessin dans laquelle seront dessinées les figures.
	 * On a besoin d'une référence à la zone de dessin (contrairement aux
	 * autres widgets) car il faut lui affecter un xxxCreationListener en
	 * fonction de la figure choisie dans la liste des figures possibles.
	 */
	protected DrawingPanel drawingPanel;

	/**
	 * Le creationListener à mettre en place dans le drawingPanel en fonction
	 * du type de figure choisie;
	 */
	protected AbstractCreationListener creationListener;

	/**
	 * Le label dans la barre d'état en bas dans lequel on affiche les
	 * conseils utilisateur pour créer une figure
	 */
	protected JLabel infoLabel;

	/**
	 * L'index de l'élément sélectionné par défaut pour le type de figure
	 */
	private final static int defaultFigureTypeIndex = 0;

	/**
	 * Les noms des couleurs de remplissage à utiliser pour remplir
	 * la [labeled]combobox des couleurs de remplissage
	 */
	protected final static String[] fillColorNames = {
		"Black",
		"White",
		"Red",
		"Orange",
		"Yellow",
		"Green",
		"Cyan",
		"Blue",
		"Magenta",
		"Others",
		"None"
	};

	/**
	 * Les couleurs de remplissage à utiliser en fonction de l'élément
	 * sélectionné dans la [labeled]combobox des couleurs de remplissage
	 */
	protected final static Paint[] fillPaints = {
		Color.black,
		Color.white,
		Color.red,
		Color.orange,
		Color.yellow,
		Color.green,
		Color.cyan,
		Color.blue,
		Color.magenta,
		null, // Color selected by a JColorChooser
		null // No Color
	};

	/**
	 * L'index de l'élément sélectionné par défaut dans les couleurs de
	 * remplissage
	 */
	private final static int defaultFillColorIndex = 0; // black

	/**
	 * L'index de la couleur de remplissage à choisir avec un
	 * {@link JColorChooser} fournit par la {@link PaintFactory}
	 */
	private final static int specialFillColorIndex = 9;

	/**
	 * Les noms des couleurs de trait à utiliser pour remplir
	 * la [labeled]combobox des couleurs de trait
	 */
	protected final static String[] edgeColorNames = {
		"Magenta",
		"Red",
		"Orange",
		"Yellow",
		"Green",
		"Cyan",
		"Blue",
		"Black",
		"Others"
	};

	/**
	 * Les couleurs de trait à utiliser en fonction de l'élément
	 * sélectionné dans la [labeled]combobox des couleurs de trait
	 */
	protected final static Paint[] edgePaints = {
		Color.magenta,
		Color.red,
		Color.orange,
		Color.yellow,
		Color.green,
		Color.cyan,
		Color.blue,
		Color.black,
		null // Color selected by a JColorChooser
	};

	/**
	 * L'index de l'élément sélectionné par défaut dans les couleurs de
	 * trait
	 */
	private final static int defaultEdgeColorIndex = 6; // blue;

	/**
	 * L'index de la couleur de remplissage à choisir avec un
	 * {@link JColorChooser} fournit par la {@link PaintFactory}
	 */
	private final static int specialEdgeColorIndex = 8;

	/**
	 * L'index de l'élément sélectionné par défaut dans les types de
	 * trait
	 */
	private final static int defaultEdgeTypeIndex = 1; // solid

	/**
	 * La largeur de trait par défaut
	 */
	private final static int defaultEdgeWidth = 4;

	/**
	 * Largeur de trait minimum
	 */
	private final static int minEdgeWidth = 1;

	/**
	 * Largeur de trait maximum
	 */
	private final static int maxEdgeWidth = 30;

	/**
	 * l'incrément entre deux largeurs de trait
	 */
	private final static int stepEdgeWidth = 1;

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton quit ou sur l'item
	 * de menu quit
	 */
	private final Action quitAction = new QuitAction();

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton undo ou sur l'item
	 * de menu undo
	 */
	private final Action undoAction = new UndoAction();

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton clear ou sur l'item
	 * de menu clear
	 */
	private final Action clearAction = new ClearAction();

	/**
	 * Action déclenchée lorsque l'on clique sur le bouton about ou sur l'item
	 * de menu about
	 */
	private final Action aboutAction = new AboutAction();
	
	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des cercles
	 */
	private final Action circleFilterAction = new ShapeFilterAction(FigureType.CIRCLE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des ellipse
	 */
	private final Action ellipseFilterAction = new ShapeFilterAction(FigureType.ELLIPSE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des rectangles
	 */
	private final Action rectangleFilterAction = new ShapeFilterAction(FigureType.RECTANGLE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des rectangles arrondis
	 */
	private final Action rRectangleFilterAction = new ShapeFilterAction(FigureType.ROUNDED_RECTANGLE);

	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des polygones
	 */
	private final Action polyFilterAction = new ShapeFilterAction(FigureType.POLYGON);
	
	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des type de lignes vides
	 */
	private final Action noneLineFilterAction = new LineFilterAction(LineType.NONE);
	
	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des type de lignes pleines
	 */
	private final Action solidLineFilterAction = new LineFilterAction(LineType.SOLID);
	
	/**
	 * Action déclenchée lorsque l'on clique sur l'item de menu de filtrage
	 * des type de lignes pointillées
	 */
	private final Action dashedLineFilterAction = new LineFilterAction(LineType.DASHED);

	/**
	 * Constructeur de la fenètre de l'éditeur.
	 * Construit les widgets et assigne les actions et autres listeners
	 * aux widgets
	 * @throws HeadlessException
	 */
	public EditorFrame() throws HeadlessException
	{
		boolean isMacOS = System.getProperty("os.name").startsWith("Mac OS");

		/*
		 * Construire l'interface graphique en utilisant WindowBuilder:
		 * Menu Contextuel -> Open With -> WindowBuilder Editor puis
		 * aller dans l'onglet Design
		 */
		setPreferredSize(new Dimension(650, 450));
		drawingModel = new Drawing();
		creationListener = null;

		setTitle(EditorName);
		if (!isMacOS)
		{
			setIconImage(Toolkit.getDefaultToolkit().getImage(
					EditorFrame.class.getResource("/images/Logo.png")));
		}


		// --------------------------------------------------------------------
		// Toolbar en haut
		// --------------------------------------------------------------------
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton btnCancel = new JButton("Undo");
		btnCancel.setAction(undoAction);
		toolBar.add(btnCancel);

		JButton btnErase = new JButton("Erase");
		btnErase.setAction(clearAction);
		toolBar.add(btnErase);

		JButton btnAbout = new JButton("About");
		btnAbout.setAction(aboutAction);
		toolBar.add(btnAbout);

		Component toolBoxSpringer = Box.createHorizontalGlue();
		toolBar.add(toolBoxSpringer);

		JButton btnClose = new JButton("Close");
		btnClose.setAction(quitAction);
		toolBar.add(btnClose);

		// --------------------------------------------------------------------
		// Barre d'état en bas
		// --------------------------------------------------------------------
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

		infoLabel = new JLabel(AbstractCreationListener.defaultTip);
		bottomPanel.add(infoLabel);

		Component horizontalGlue = Box.createHorizontalGlue();
		bottomPanel.add(horizontalGlue);

		JLabel coordsLabel = new JLabel(DrawingPanel.defaultCoordString);
		bottomPanel.add(coordsLabel);

		// --------------------------------------------------------------------
		// Panneau de contrôle à gauche
		// --------------------------------------------------------------------
		JPanel leftPanel = new JPanel();
		leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		getContentPane().add(leftPanel, BorderLayout.WEST);

		JLabeledComboBox figureTypeCombobox = new JLabeledComboBox("Shape",
				FigureType.stringValues(), defaultFigureTypeIndex,
				(ItemListener) null);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(figureTypeCombobox);

		JLabeledComboBox fillPaintComboBox = new JLabeledComboBox("Fill Color",
				fillColorNames, defaultFillColorIndex, (ItemListener) null);
		leftPanel.add(fillPaintComboBox);

		JLabeledComboBox edgePaintComboBox = new JLabeledComboBox("Edge Color",
				edgeColorNames, defaultEdgeColorIndex, (ItemListener) null);
		leftPanel.add(edgePaintComboBox);

		JLabeledComboBox edgeTypeCombobox = new JLabeledComboBox("Line Type",
				LineType.stringValues(), defaultEdgeTypeIndex,
				(ItemListener) null);
		leftPanel.add(edgeTypeCombobox);

		JPanel edgeWidthPanel = new JPanel();
		edgeWidthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(edgeWidthPanel);
		edgeWidthPanel.setLayout(new BoxLayout(edgeWidthPanel, BoxLayout.X_AXIS));

		JLabel edgeWidthLabel = new JLabel("Line Width");
		edgeWidthPanel.add(edgeWidthLabel);

		JSpinner edgeWidthSpinner = new JSpinner();
		edgeWidthSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
		SpinnerNumberModel snm = new SpinnerNumberModel(defaultEdgeWidth,
		                                                minEdgeWidth,
		                                                maxEdgeWidth,
		                                                stepEdgeWidth);
		edgeWidthSpinner.setModel(snm);
		edgeWidthPanel.add(edgeWidthSpinner);

		InfoPanel infoPanel = new InfoPanel();
		infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		leftPanel.add(infoPanel);

		// --------------------------------------------------------------------
		// Zone de dessin
		// --------------------------------------------------------------------
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		drawingPanel = new DrawingPanel(drawingModel, coordsLabel, infoPanel);
		scrollPane.setViewportView(drawingPanel);

		// --------------------------------------------------------------------
		// Barre de menus
		// --------------------------------------------------------------------
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("Drawing");
		menuBar.add(mnFile);

		JMenuItem mntmCancel = new JMenuItem("Cancel");
		mntmCancel.setAction(undoAction);
		mnFile.add(mntmCancel);

		JMenuItem mntmClear = new JMenuItem("Clear");
		mntmClear.setAction(clearAction);
		mnFile.add(mntmClear);
		
		JMenu mnFilter = new JMenu("Filter");
		menuBar.add(mnFilter);
		
		JCheckBoxMenuItem chckbxmntmFiltering = new JCheckBoxMenuItem("Filtering");
		chckbxmntmFiltering.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.META_MASK));
		chckbxmntmFiltering.setSelected(drawingModel.getFiltering());
		chckbxmntmFiltering.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent event) 
			{
				AbstractButton button = (AbstractButton) event.getSource();
				boolean selected = button.getModel().isSelected();
				drawingModel.setFiltering(selected);
			}
		});
		mnFilter.add(chckbxmntmFiltering);
		
		JMenu mnFigures = new JMenu("Figures");
		mnFilter.add(mnFigures);
		
		JCheckBoxMenuItem chckbxmntmCircles = new JCheckBoxMenuItem("Circles");
		chckbxmntmCircles.setAction(circleFilterAction);
		mnFigures.add(chckbxmntmCircles);
		
		JCheckBoxMenuItem chckbxmntmEllipses = new JCheckBoxMenuItem("Ellipses");
		chckbxmntmEllipses.setAction(ellipseFilterAction);
		mnFigures.add(chckbxmntmEllipses);
		
		JCheckBoxMenuItem chckbxmntmRectangles = new JCheckBoxMenuItem("Rectangles");
		chckbxmntmRectangles.setAction(rectangleFilterAction);
		mnFigures.add(chckbxmntmRectangles);
		
		JCheckBoxMenuItem chckbxmntmRoundedRectangles = new JCheckBoxMenuItem("Rounded Rectangles");
		chckbxmntmRoundedRectangles.setAction(rRectangleFilterAction);
		mnFigures.add(chckbxmntmRoundedRectangles);
		
		JCheckBoxMenuItem chckbxmntmPolygons = new JCheckBoxMenuItem("Polygons");
		chckbxmntmPolygons.setAction(polyFilterAction);
		mnFigures.add(chckbxmntmPolygons);
		
		JMenu mnColors = new JMenu("Colors");
		mnFilter.add(mnColors);
		
		JCheckBoxMenuItem chckbxmntmFillColor = new JCheckBoxMenuItem("Fill Color");
		chckbxmntmFillColor.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent event) 
			{
				AbstractButton button = (AbstractButton) event.getSource();
				boolean selected = button.getModel().isSelected();
				if (selected)
				{
					// Choose color from dialog
					// Paint newPaint = PaintFactory.getPaint(PaintFactory.getPaint(button.getParent(), "Choose Color", (Color) drawingModel.getFillpaint()));
					drawingModel.setFillColorFilter(new FillColorFilter(drawingModel.getFillpaint()));
				}
				else
				{
					drawingModel.setFillColorFilter(null);
				}
			}
		});
		chckbxmntmFillColor.setIcon(IconFactory.getIcon("FillColor_small"));
		mnColors.add(chckbxmntmFillColor);
		
		JCheckBoxMenuItem chckbxmntmEdgeColor = new JCheckBoxMenuItem("Edge Color");
		chckbxmntmEdgeColor.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent event) 
			{
				AbstractButton button = (AbstractButton) event.getSource();
				boolean selected = button.getModel().isSelected();
				if (selected)
				{
					// Choose color from dialog
					// Paint newPaint = PaintFactory.getPaint(PaintFactory.getPaint(button.getParent(), "Choose Color", (Color) drawingModel.getEdgePaint()));
					drawingModel.setEdgeColorFilter(new EdgeColorFilter(drawingModel.getEdgePaint()));
				}
				else
				{
					drawingModel.setEdgeColorFilter(null);
				}
			}
		});
		chckbxmntmEdgeColor.setIcon(IconFactory.getIcon("EdgeColor_small"));
		mnColors.add(chckbxmntmEdgeColor);
		
		JMenu mnStrokes = new JMenu("Strokes");
		mnFilter.add(mnStrokes);
		
		JCheckBoxMenuItem chckbxmntmNone = new JCheckBoxMenuItem("None");
		chckbxmntmNone.setAction(noneLineFilterAction);
		chckbxmntmNone.setIcon(IconFactory.getIcon("None_small"));
		mnStrokes.add(chckbxmntmNone);
		
		JCheckBoxMenuItem chckbxmntmSolid = new JCheckBoxMenuItem("Solid");
		chckbxmntmSolid.setAction(solidLineFilterAction);
		chckbxmntmSolid.setIcon(IconFactory.getIcon("Solid_small"));
		mnStrokes.add(chckbxmntmSolid);
		
		JCheckBoxMenuItem chckbxmntmDashed = new JCheckBoxMenuItem("Dashed");
		chckbxmntmDashed.setAction(dashedLineFilterAction);
		chckbxmntmDashed.setIcon(IconFactory.getIcon("Dashed_small"));
		mnStrokes.add(chckbxmntmDashed);

		if (!isMacOS)
		{
			JSeparator separator = new JSeparator();
			mnFile.add(separator);

			JMenuItem mntmQuit = new JMenuItem("Quit");
			mntmQuit.setAction(quitAction);
			mnFile.add(mntmQuit);

			JMenu mnHelp = new JMenu("Help");
			menuBar.add(mnHelp);

			JMenuItem mntmAbout = new JMenuItem("About...");
			mntmAbout.setAction(aboutAction);
			mnHelp.add(mntmAbout);
		}


		// --------------------------------------------------------------------
		// Ajout des contrôleurs aux widgets
		// pour connaître les Listeners applicable à un widget
		// dans WindowBuilder, sélectionnez un widger de l'UI puis Menu
		// Conextuel -> Add event handler
		// --------------------------------------------------------------------
		figureTypeCombobox.addItemListener(
				new ShapeItemListener(
						FigureType.fromInteger(
								figureTypeCombobox.getSelectedIndex())));

		fillPaintComboBox.addItemListener(new ColorItemListener(fillPaints,
				fillPaintComboBox.getSelectedIndex(), specialFillColorIndex,
				PaintToType.FILL));

		edgePaintComboBox.addItemListener(new ColorItemListener(edgePaints,
				edgePaintComboBox.getSelectedIndex(), specialEdgeColorIndex,
				PaintToType.EDGE));

		edgeTypeCombobox.addItemListener(new EdgeTypeListener(LineType
				.fromInteger(edgeTypeCombobox.getSelectedIndex())));

		edgeWidthSpinner.addChangeListener(
				new EdgeWidthListener(defaultEdgeWidth));

//		if (isMacOS)
//		{
//			/*
//			 * Ajout des about et quit handlers pour MacOS
//			 */
//			Application.getApplication().setQuitHandler(
//					(QuitHandler) quitAction);
//			Application.getApplication().setAboutHandler(
//					(AboutHandler) aboutAction);
//		}
	}

	/**
	 * Action pour quitter l'application
	 * @author davidroussel
	 */
	private class QuitAction extends AbstractAction // implements QuitHandler
	{
		/**
		 * Constructeur de l'action pour quitter l'application.
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public QuitAction()
		{
			putValue(NAME, "Quit");
			/*
			 * Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
			 * 	= InputEvent.CTRL_MASK on win/linux
			 *  = InputEvent.META_MASK on mac os
			 */
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Quit"));
			putValue(SMALL_ICON, IconFactory.getIcon("Quit_small"));
			putValue(SHORT_DESCRIPTION, "Quits the application");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			doQuit();
		}

//		/**
//		 * Opérations réalisées par le quit handler
//		 * @param e l'évènement de quit
//		 * @param r la réponse au quit
//		 */
//		@Override
//		public void handleQuitRequestWith(QuitEvent e, QuitResponse r)
//		{
//			doQuit();
//		}

		/**
		 * Action réalisée pour quitter dans un {@link Action}
		 */
		public void doQuit()
		{
			/*
			 * Action à effectuer lorsque l'action "undo" est cliquée :
			 * sortir avec un System.exit() (pas très propre, mais fonctionne)
			 */
			System.exit(0);
		}
	}

	/**
	 * Action réalisée pour effacer la dernière figure du dessin.
	 */
	private class UndoAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action effacer la dernière figure du dessin
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public UndoAction()
		{
			putValue(NAME, "Undo");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("Undo"));
			putValue(SMALL_ICON, IconFactory.getIcon("Undo_small"));
			putValue(SHORT_DESCRIPTION, "Undo last drawing");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*
			 * Action à effectuer lorsque l'action "undo" est cliquée :
			 * retirer la dernière figure dessinée
			 */
			drawingModel.removeLastFigure();
		}
	}

	/**
	 * Action réalisée pour effacer toutes les figures du dessin
	 */
	private class ClearAction extends AbstractAction
	{
		/**
		 * Constructeur de l'action pour effacer toutes les figures du dessin
		 * Met en place le raccourci clavier, l'icône et la description
		 * de l'action
		 */
		public ClearAction()
		{
			putValue(NAME, "Clear");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			putValue(LARGE_ICON_KEY,IconFactory.getIcon("Delete"));
			putValue(SMALL_ICON, IconFactory.getIcon("Delete_small"));
			putValue(SHORT_DESCRIPTION, "Erase all drawings");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			/*
			 * Action à effectuer lorsque l'action "clear" est cliquée :
			 * Effacer toutes les figures du dessin
			 */
			drawingModel.clear();
		}
	}

	/**
	 * Action réalisée pour afficher la boite de dialogue "A propos ..."
	 */
	private class AboutAction extends AbstractAction // implements AboutHandler
	{
		/**
		 * Constructeur de l'action pour afficher la boite de dialogue
		 * "A propos ..." Met en place le raccourci clavier, l'icône et la
		 * description de l'action
		 */
		public AboutAction() 
		{
			putValue(LARGE_ICON_KEY, IconFactory.getIcon("About"));
			putValue(SMALL_ICON, IconFactory.getIcon("About_small"));
			putValue(NAME, "About");
			putValue(SHORT_DESCRIPTION, "App information");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param e l'évènement déclenchant l'action. Peut provenir d'un bouton
		 *            ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			doAbout(e);
		}

//		/**
//		 * Actions réalisées par le about handler
//		 * @param e l'évènement déclenchant le about handler
//		 */
//		@Override
//		public void handleAbout(AboutEvent e)
//		{
//			doAbout(e);
//		}

		/**
		 * Action réalisée pour "A propos" dans un {@link Action}
		 * @param e l'évènement ayant déclenché l'action
		 */
		public void doAbout(EventObject e)
		{
			/*
			 * Action à effectuer lorsque l'action "about" est cliquée :
			 * Ouvrir un MessageDialog (JOptionPane.showMessageDialog(...)) de
			 * type JOptionPane.INFORMATION_MESSAGE
			 */
			Object source = e.getSource();
			Component component = (source instanceof Component ?
					(Component)source : null);
			JOptionPane.showMessageDialog(component, EditorName,
					"About ...", JOptionPane.INFORMATION_MESSAGE);

		}
	}
	
	/**
	 * Action réalisée pour ajouter ou retirer un filtre de type de figure
	 */
	private class ShapeFilterAction extends AbstractAction // implements AboutHandler
	{		
		/**
		 * Le type de figure
		 */
		private FigureType type;
		
		/**
		 * Constructeur de l'action pour mettre en place ou enlever un filtre
		 * pour filtrer les types de figures
		 */
		public ShapeFilterAction(FigureType type) 
		{
			this.type = type;
			String name = type.toString();
			putValue(LARGE_ICON_KEY, IconFactory.getIcon(name));
			putValue(SMALL_ICON, IconFactory.getIcon(name + "_small"));
			putValue(NAME, name);
			putValue(SHORT_DESCRIPTION, "Set/unset " + name  + " filter");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param event l'évènement déclenchant l'action. Peut provenir d'un 
		 * bouton ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			AbstractButton button = (AbstractButton) event.getSource();
			boolean selected = button.getModel().isSelected();
			ShapeFilter sf = new ShapeFilter(type);
			if (selected)
			{
				drawingModel.addShapeFilter(sf);
			}
			else
			{
				drawingModel.removeShapeFilter(sf);
			}
		}
	}
	
	/**
	 * Action réalisée pour ajouter ou retirer un filtre de type trait de figure
	 */
	private class LineFilterAction extends AbstractAction // implements AboutHandler
	{		
		/**
		 * Le type de trait de la figure
		 */
		private LineType type;
		
		/**
		 * Constructeur de l'action pour mettre en place ou enlever un filtre
		 * pour filtrer les types de figures
		 */
		public LineFilterAction(LineType type) 
		{
			this.type = type;
			String name = type.toString();
			putValue(LARGE_ICON_KEY, IconFactory.getIcon(name));
			putValue(SMALL_ICON, IconFactory.getIcon(name + "_small"));
			putValue(NAME, name);
			putValue(SHORT_DESCRIPTION, "Set/unset " + name  + " filter");
		}

		/**
		 * Opérations réalisées par l'action
		 * @param event l'évènement déclenchant l'action. Peut provenir d'un 
		 * bouton ou d'un item de menu
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			AbstractButton button = (AbstractButton) event.getSource();
			boolean selected = button.getModel().isSelected();
			LineFilter lf = new LineFilter(type);
			if (selected)
			{
				drawingModel.addLineFilter(lf);
			}
			else
			{
				drawingModel.removeLineFilter(lf);
			}
		}
	}

	/**
	 * Contrôleur d'évènement permettant de modifier le type de figures à
	 * dessiner.
	 * @note dépends de #drawingModel et #infoLabel qui doivent être non
	 * null avant instanciation
	 */
	private class ShapeItemListener implements ItemListener
	{
		/**
		 * Constructeur valué du contrôleur.
		 * Initialise le type de dessin dans {@link EditorFrame#drawingModel}
		 * et crée le {@link AbstractCreationListener} correspondant.
		 * @param initialIndex l'index du type de forme sélectionné afin de
		 * mettre en place le bon creationListener dans le
		 * {@link EditorFrame#drawingPanel}.
		 */
		public ShapeItemListener(FigureType type)
		{
			// Mise en place du type de figure
			drawingModel.setType(type);

			// Mise en place du type de creationListener
			creationListener = type.getCreationListener(drawingModel,
                                                        infoLabel);
			drawingPanel.addCreationListener(creationListener);
		}

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			JComboBox<?> items = (JComboBox<?>) e.getSource();
			int index = items.getSelectedIndex();
			int stateChange = e.getStateChange();
			FigureType figureType = FigureType.fromInteger(index);
			switch (stateChange)
			{
				case ItemEvent.SELECTED:
					// Mise en place d'un nouveau type de figure
					drawingModel.setType(figureType);

					// Mise en place d'un nouveau type de creationListener
					// Après avoir retiré l'ancien
					drawingPanel.removeCreationListener(creationListener);
					creationListener = figureType.getCreationListener(drawingModel, infoLabel);
					drawingPanel.addCreationListener(creationListener);
					break;
			}
		}
	}

	/**
	 * Contrôleur d'évènements permettant de modifier la couleur du trait
	 * @note utilise #drawingModel qui doit être non null avant instanciation
	 */
	private class ColorItemListener implements ItemListener
	{
		/**
		 * Ce à quoi s'applique la couleur choisie.
		 * Soit au rmplissage, soit au trait.
		 */
		private PaintToType applyTo;

		/**
		 * La dernière couleur choisie (pour le {@link JColorChooser})
		 */
		private Color lastColor;

		/**
		 * Le tableau des couleurs possibles
		 */
		private Paint[] colors;

		/**
		 * L'index de la couleur spéciale à choisir avec un {@link JColorChooser}
		 */
		private final int customColorIndex;

		/**
		 * L'index de la dernière couleur sélectionnée dans le combobox.
		 * Afin de pouvoir y revenir si jamais le {@link JColorChooser} est
		 * annulé.
		 */
		private int lastSelectedIndex;

		/**
		 * la couleur choisie
		 */
		private Paint paint;

		/**
		 * Constructeur du contrôleur d'évènements d'un combobox permettant
		 * de choisir la couleur de templissage
		 * @param colors le tableau des couleurs possibles
		 * @param selectedIndex l'index de l'élément actuellement sélectionné
		 * @param customColorIndex l'index de la couleur spéciale parmis les
		 * colors à définir à l'aide d'un {@link JColorChooser}.
		 * @param applyTo Ce à quoi s'applique la couleur (le remplissage ou
		 * bien le trait)
		 */
		public ColorItemListener(Paint[] colors,
		                         int selectedIndex,
		                         int customColorIndex,
		                         PaintToType applyTo)
		{
			this.colors = colors;
			lastSelectedIndex = selectedIndex;
			this.customColorIndex = customColorIndex;
			this.applyTo = applyTo;
			lastColor = (Color) colors[selectedIndex];
			paint = colors[selectedIndex];

			applyTo.applyPaintTo(paint, drawingModel);
		}

		/**
		 * Actions à réaliser lorsque l'élément sélectionné du combox change
		 * @param e l'évènement de changement d'item du combobox
		 */
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			JComboBox<?> combo = (JComboBox<?>) e.getSource();
			int index = combo.getSelectedIndex();

			if ((index >= 0) && (index < colors.length))
			{
				if ((e.getStateChange() == ItemEvent.SELECTED))
				{
					// New color has been selected
					if (index == customColorIndex) // Custom color from chooser
					{
						Paint chosenColor = PaintFactory.getPaint(combo, 
							"Choose " + applyTo.toString() + " Color", 
							lastColor);
						if (chosenColor != null)
						{
							paint = chosenColor;
						}
						else
						{
							// ColorChooser has been cancelled we should go
							// back to last selected index
							combo.setSelectedIndex(lastSelectedIndex);

							// paint does not change
						}
					}
					else // regular color
					{
						paint = colors[index];
					}

					lastColor = (Color)paint;
					applyTo.applyPaintTo(paint, drawingModel);
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					// Old color has been delesected
					if ((index >= 0 ) && (index < customColorIndex))
					{
						lastColor = (Color)edgePaints[index];
						lastSelectedIndex = index;
					}
				}
			}
			else
			{
				System.err.println("Unknown " + applyTo.toString()
						+ " color index : " + index);
			}
		}
	}

	/**
	 * Contrôleur d'évènements permettant de modifier le type de trait (normal,
	 * pointillé, sans trait)
	 * @note utilise #drawingModel qui doit être non null avant instanciation
	 */
	private class EdgeTypeListener implements ItemListener
	{
		/**
		 * Le type de trait à mettre en place
		 */
		private LineType edgeType;

		public EdgeTypeListener(LineType type)
		{
			edgeType = type;
			drawingModel.setEdgeType(edgeType);
		}

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			JComboBox<?> items = (JComboBox<?>) e.getSource();
			int index = items.getSelectedIndex();

			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				// actions à réaliser lorsque le type de trait change
				LineType type = LineType.fromInteger(index);
				drawingModel.setEdgeType(type);
			}
		}
	}

	/**
	 * Contrôleur d'évènement permettant de modifier la taille du trait
	 * en fonction des valeurs d'un {@link JSpinner}
	 */
	private class EdgeWidthListener implements ChangeListener
	{
		/**
		 * Constructeur du contrôleur d'évènements contrôlant l'épaisseur du
		 * trait
		 * @param initialValue la valeur initiale de la largeur du trait à
		 * appliquer au dessin (EditorFrame#drawingModel)
		 */
		public EdgeWidthListener(int initialValue)
		{
			drawingModel.setEdgeWidth(initialValue);
		}

		/**
		 * Actions à réaliser lorsque la valeur du spinner change
		 * @param e l'évènement de changement de valeur du spinner
		 */
		@Override
		public void stateChanged(ChangeEvent e)
		{
			JSpinner spinner = (JSpinner) e.getSource();
			SpinnerNumberModel spinnerModel =
					(SpinnerNumberModel) spinner.getModel();

			drawingModel.setEdgeWidth(spinnerModel.getNumber().floatValue());
		}
	}
}
