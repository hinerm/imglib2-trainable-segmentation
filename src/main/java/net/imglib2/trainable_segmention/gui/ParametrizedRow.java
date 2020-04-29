package net.imglib2.trainable_segmention.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.scijava.Context;

import net.imglib2.trainable_segmention.pixel_feature.settings.FeatureSetting;
import net.imglib2.trainable_segmention.pixel_feature.settings.GlobalSettings;

public class ParametrizedRow extends JPanel implements SelectableRow {

	private static final ImageIcon DUP_ICON = IconResources.getIcon( "plus_icon_16px.png" );
	private static final ImageIcon INFO_ICON = IconResources.getIcon( "info_icon_16px.png" );

	private GlobalSettings globalSettings;
	private Context context;

	private FeatureSetting featureSetting;
	private JCheckBox checkbox;
	private GridBagConstraints gbc;

	public ParametrizedRow( Context context, GlobalSettings globalSettings, FeatureSetting featureSetting ) {
		this.context = context;
		this.globalSettings = globalSettings;
		this.featureSetting = featureSetting;
		setLayout( new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		initUI();
		setGlobalSettings(globalSettings);
	}

	private void initUI() {
		JPanel titleRow = new JPanel();
		titleRow.setLayout( new BorderLayout() );
		titleRow.add( Box.createHorizontalStrut(30), BorderLayout.WEST );

		checkbox = new JCheckBox( featureSetting.getName() );
		checkbox.addActionListener( this::checkForParameterRow );
		titleRow.add( checkbox, BorderLayout.CENTER );

		JPanel btnPanel = createButtonPanel();
		titleRow.add( btnPanel, BorderLayout.EAST );
		add( titleRow, gbc);
		gbc.gridy = GridBagConstraints.RELATIVE;
		add( new ParametersRow( context, globalSettings, featureSetting), gbc );
	}

	private JPanel createButtonPanel() {
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		btnPanel.add(createButton(DUP_ICON, "Duplicate filter", this::duplicate));
		btnPanel.add(createButton(INFO_ICON, "Filter information", this::showInfoDialog));
		return btnPanel;
	}

	private JButton createButton(ImageIcon infoIcon, String filter_information, ActionListener showInfoDialog) {
		JButton infoButton = new JButton(infoIcon);
		infoButton.setFocusPainted(false);
		infoButton.setMargin(new Insets(0, 0, 0, 0));
		infoButton.setContentAreaFilled(false);
		infoButton.setBorderPainted(false);
		infoButton.setOpaque(false);
		infoButton.setToolTipText(filter_information);
		infoButton.addActionListener(showInfoDialog);
		return infoButton;
	}


	private void showInfoDialog( ActionEvent e ) {
		InfoDialog docoDiag = new InfoDialog( this, "example", "If you use this filter you will do great things" );
		docoDiag.setVisible( true );
	}

	private void duplicate( ActionEvent e ) {
		add( new ParametersRow( context, globalSettings, FeatureSetting.copy( featureSetting )), gbc );
		revalidate();
		repaint();
	}
	
	private void checkForParameterRow (ActionEvent e) {
		if (getComponents().length == 1) {
			add( new ParametersRow( context, globalSettings, featureSetting), gbc );
			revalidate();
			repaint();
		}
	}

	@Override
	public void remove( Component c ) {
		super.remove( c );
		revalidate();
		repaint();
	}
	
	@Override
	public List< FeatureSetting > getSelectedFeatureSettings() {
		List<FeatureSetting> selected = new ArrayList<>();
		if (checkbox.isSelected())
		{
			Component[] children = getComponents(); 
			for( Component child: children) {
				if (child instanceof ParametersRow )
					selected.add( ( ( ParametersRow ) child ).getFeatureSetting());
			}
		}
		return selected;
	}

	@Override
	public void setGlobalSettings(GlobalSettings globalSettings) {
		try {
			this.globalSettings = globalSettings;
			boolean isValid = featureSetting.pluginClass().newInstance().checkGlobalSettings(globalSettings);
			enableRecursively(this, isValid);
		} catch (InstantiationException | IllegalAccessException ignored) {}
	}

	private static void enableRecursively(Component component, boolean enabled) {
		component.setEnabled(enabled);
		if(component instanceof JPanel)	{
			JPanel panel = (JPanel) component;
			for(Component child : panel.getComponents())
				enableRecursively(child, enabled);
		}
	}
}
