package com.purehero.bithumb.trader.bot;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.purehero.bithumb.api.BithumbLastTicker;
import com.purehero.bithumb.api.BithumbMyBalanceInfo;
import com.purehero.bithumb.util.Api_Client;
import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;


public class Main extends javafx.application.Application {
//public class Main {
	private final String apk_key 	= "64346e7c6622346faa0cff0ac46c73a3"; 
	private final String secure_key = "79639ec221ba0c958eef5d66a7f8fdaa";
	private Api_Client api = new Api_Client( apk_key, secure_key );
	
	private BithumbMyBalanceInfo balanceInfo = new BithumbMyBalanceInfo(); 
	//private BithumbMyTicker tickerInfo = new BithumbMyTicker();
	private BithumbLastTicker requestLastTicker = new BithumbLastTicker();
	//private BithumbOrderBook  requestOrderBook 	= new BithumbOrderBook();
	
	private ObservableList<PriceData> priceTableDatas;
	private boolean threadFlag = true;
	private int lastPrices [] = null;
	
	@FXML
	private TableView<PriceData> tvPriceTable;
	
	@FXML
	private Label lbTotalCache;
	
	@FXML
	private LineChart<NumberAxis, NumberAxis> lcLineChart;
	
	@SuppressWarnings("rawtypes")
	Series series [] = new Series[ CURRENCY_DEF.MAX_CURRENCY ];
	
	public static void main(String args[]) {
		launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent mainEx = FXMLLoader.load( Main.this.getClass().getResource("main.fxml"));
	
		primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent arg0) {
				threadFlag = false;
				System.exit(0);
			}});
		
		Scene scene = new Scene( mainEx );
		primaryStage.setTitle( "Android Touch Macro v2.0" );
		primaryStage.setScene( scene );
		primaryStage.setResizable(false);
		primaryStage.show();
		
		
	}
	
	@Override
	public void stop(){
		threadFlag = false;
		System.exit(0);
	}
		
	@SuppressWarnings("unchecked")
	@FXML
    public void initialize() {
		int column_index = 0;
		TableColumn<PriceData, String> tcCurrencyName 	= (TableColumn<PriceData, String>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyName.setCellValueFactory( new PropertyValueFactory<PriceData, String>( "currencyName" ));
		tcCurrencyName.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<PriceData, String> tcCurrencyUnits 	= (TableColumn<PriceData, String>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyUnits.setCellValueFactory( new PropertyValueFactory<PriceData, String>( "currencyUnitsString" ));
		tcCurrencyUnits.setStyle("-fx-alignment: CENTER-RIGHT;");
	
		TableColumn<PriceData, Integer> tcCurrencyLastPrice = (TableColumn<PriceData, Integer>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyLastPrice.setCellValueFactory( new PropertyValueFactory<PriceData, Integer>( "currencyLastPriceFormatString" ));
		tcCurrencyLastPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
		
		TableColumn<PriceData, Integer> tcCurrencyPrice = (TableColumn<PriceData, Integer>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyPrice.setCellValueFactory( new PropertyValueFactory<PriceData, Integer>( "currencyPriceFormatString" ));
		tcCurrencyPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
		
		priceTableDatas = FXCollections.observableArrayList( new ArrayList<PriceData>() ); 
		tvPriceTable.setItems( priceTableDatas );
		
		for( int i = 0; i < CURRENCY_DEF.MAX_CURRENCY; i++ ) {
			series [i] = new Series();
		}
		lcLineChart.getData().add( series[ selectedCurrency ] );
		
		new Thread( traderBotThreadRunnable ).start();
	}
		
	Runnable traderBotThreadRunnable = new Runnable() {
		public void run() {
			balanceInfo.requestAPI( api );
			
			while( threadFlag ) {
				if( requestLastTicker.requestAPI( api )) {
					//System.out.println( requestLastTicker.toInfoString() );
					lastPrices 		= requestLastTicker.getLastPriceInfos();

					/*
					sleepMillisecond( 300 );
					if( requestOrderBook.requestAPI( api )) {
						System.out.println( requestOrderBook.getResponseJSonString() );
					}
					OrderBookData lastOrderBookDatas [] = requestOrderBook.getOrderBookDatas();
					*/
				}
				Platform.runLater( uiUpdateRunnable );
				sleepMillisecond( 500 );
			}
		}
	};
	
	
	Runnable uiUpdateRunnable = new Runnable() {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void run() {
			priceTableDatas.clear();
			
			int myTotalCache = 0;
			double balances[] = balanceInfo.getBalances();
			for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
				int cache = (int)( balances[ idxCurrency ] * lastPrices[ idxCurrency ]);
				
				PriceData priceData = new PriceData();
				priceData.setCurrencyName( CURRENCY_DEF.strCurrencies[ idxCurrency ] );
				priceData.setCurrencyUnits( balances[ idxCurrency ] );
				priceData.setCurrencyLastPrice( lastPrices[ idxCurrency ] );
				priceData.setCurrencyPrice( cache );
				
				myTotalCache += cache;
				
				priceTableDatas.add( priceData );
				XYChart.Data newData = new XYChart.Data( ""+tmpCount, ( lastPrices[ idxCurrency ] ));					
				
				series[idxCurrency].getData().add( newData );				
			}
			
			tmpCount++;
			
			lbTotalCache.setText( CurrencyUtil.getIntegerToFormatString( myTotalCache ));
		}
	};
	int tmpCount = 0;
	
	private void sleepMillisecond( int millisecond ) {
		try { Thread.sleep( millisecond ); } catch (InterruptedException e) { e.printStackTrace();}
	}
	
	@FXML
	private void event_handle_mouse(MouseEvent e) {
		Control ctrl = ( Control ) e.getSource();
		
		switch( ctrl.getId()) {
		case "tvPriceTable" 				: mouse_handler_table_view(e, ctrl); break;
		}		
	}

	int selectedCurrency = CURRENCY_DEF.BTC;
	private void mouse_handler_table_view(MouseEvent e, Control ctrl) {
		selectedCurrency = tvPriceTable.getSelectionModel().getSelectedIndex();
		
		lcLineChart.getData().clear();
		lcLineChart.getData().add( series[ selectedCurrency ] );
		
		lcLineChart.setTitle( CURRENCY_DEF.strCurrenciesKOR[ selectedCurrency ]  );
	}
}

