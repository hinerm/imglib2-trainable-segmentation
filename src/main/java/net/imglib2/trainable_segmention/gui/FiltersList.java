package net.imglib2.trainable_segmention.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.scijava.AbstractContextual;
import org.scijava.Context;
import org.scijava.module.Module;
import org.scijava.module.ModuleCanceledException;
import org.scijava.module.ModuleException;
import org.scijava.ui.swing.widget.SwingInputHarvester;
import org.scijava.widget.InputHarvester;

import net.imglib2.trainable_segmention.gui.FeatureSettingsUI.GlobalsPanel;
import net.imglib2.trainable_segmention.pixel_feature.settings.FeatureSetting;
import net.imglib2.trainable_segmention.pixel_feature.settings.GlobalSettings;

public class FiltersList extends JList< FiltersListRow > {

	private static final long serialVersionUID = 1L;

	private int selectedIndex;
	private final FeatureSettingsDialog featureSettingsDialog;
	private GlobalsPanel globalsPanel;

	public FiltersList( Context context, GlobalsPanel globalsPanel, FiltersListModel model ) {
		super( model );
		featureSettingsDialog = new FeatureSettingsDialog( context );
		this.globalsPanel = globalsPanel;

		setCellRenderer( new CheckBoxCellRenderer() );

		addMouseListener( new MouseAdapter() {

			@Override
			public void mousePressed( MouseEvent e ) {
				selectedIndex = locationToIndex( e.getPoint() );
				if ( selectedIndex < 0 )
					return;

				int width = getCellBounds( selectedIndex, selectedIndex ).width;

				Point p = e.getPoint().getLocation();
				if ( selectedIndex > 0 ) {
					Rectangle listCellBounds = getCellBounds( 0, selectedIndex - 1 );
					p.y -= listCellBounds.height;
				}
				System.out.println( p);

				FiltersListRow panel = getModel().getElementAt( selectedIndex );
				JCheckBox cb = panel.getCheckBox();
				JLabel ib = panel.getInfoButton();
				JLabel db = panel.getDuplicateButton();
				JLabel rb = panel.getRemoveButton();
				JLabel eb = panel.getEditButton();
				if ( isOverComponent( cb, p ) ) {
					cb.setSelected( !cb.isSelected() );
					validate();
					repaint();
				} else if ( isOverComponent( eb, p ) ) {
					editParameters();
					
				} else if ( isOverComponent( db, p ) ) {
					duplicateFilter();
					
				} else if ( isOverComponent( rb, p ) ) {
					removeFilter();
					
				} else if ( isOverComponent( ib, p ) ) {
					panel.showInfoDialog();

				}
			}

		} );

		setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	}

	private void duplicateFilter() {
		( ( FiltersListModel ) getModel() ).add( new FiltersListRow(getModel().getElementAt( selectedIndex ).getFeature(), getModel().getElementAt( selectedIndex ).isParametrized()));
		ensureIndexIsVisible(getModel().getSize() - 1);
		validate();
		repaint();
	}

	private void removeFilter() {
		( ( FiltersListModel ) getModel() ).remove( selectedIndex );
		( ( FiltersListModel ) getModel() ).update();
		validate();
		repaint();
	}

	private void editParameters() {
		featureSettingsDialog.show( getModel().getElementAt( selectedIndex ).getFeatureSetting(), globalsPanel.get() );
		( ( FiltersListModel ) getModel() ).update();
		getModel().getElementAt( selectedIndex ).update();
		validate();
		repaint();
	}

	private boolean isOverComponent( Component c, Point p ) {
		Point pp = c.getParent().getLocation();
		Rectangle r = c.getBounds();
		if (pp.x > 0) {
			r.setBounds( c.getX() + pp.x, c.getY(), c.getWidth(), c.getHeight());
		}
		return r.contains( p );
	}

	class CheckBoxCellRenderer implements ListCellRenderer< Object > {

		@Override
		public Component getListCellRendererComponent(
				JList< ? > list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus ) {

			FiltersListRow row = ( FiltersListRow ) value;

			JCheckBox checkbox = row.getCheckBox();
			checkbox.setBackground( isSelected ? list.getSelectionBackground() : list.getBackground() );
			checkbox.setForeground( isSelected ? list.getSelectionForeground() : list.getForeground() );
			checkbox.setEnabled( list.isEnabled() );
			checkbox.setFont( list.getFont() );
			checkbox.setFocusPainted( false );
			checkbox.setBorderPainted( true );
			checkbox.setBorder( isSelected ? UIManager.getBorder( "List.focusCellHighlightBorder" ) : new EmptyBorder( 1, 1, 1, 1 ) );
			return row;
		}
	}

	static class FeatureSettingsDialog extends AbstractContextual {

		@SuppressWarnings( "rawtypes" )
		private final InputHarvester harvester;

		FeatureSettingsDialog( Context context ) {
			harvester = new SwingInputHarvester();
			context.inject( harvester );
		}

		FeatureSetting show( FeatureSetting op, GlobalSettings globalSetting ) {
			try {
				Module module = op.asModule( globalSetting );
				harvester.harvest( module );
				return FeatureSetting.fromModule( module );
			} catch ( ModuleCanceledException e ) {
				return op;
			} catch ( ModuleException e ) {
				throw new RuntimeException( e );
			}
		}
	}

}
