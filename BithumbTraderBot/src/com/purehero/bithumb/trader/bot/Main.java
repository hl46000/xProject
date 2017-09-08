package com.purehero.bithumb.trader.bot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.purehero.bithumb.api.BithumbLastTicker;
import com.purehero.bithumb.api.BithumbMarketBuy;
import com.purehero.bithumb.api.BithumbMarketSell;
import com.purehero.bithumb.api.BithumbMyBalanceInfo;
import com.purehero.bithumb.api.BithumbMyTransactions;
import com.purehero.bithumb.util.Api_Client;
import com.purehero.bithumb.util.CURRENCY_DEF;
import com.purehero.bithumb.util.CurrencyUtil;
import com.purehero.bithumb.util.Util;


public class Main extends javafx.application.Application {
//public class Main {
	private final String apk_key 	= "64346e7c6622346faa0cff0ac46c73a3"; 
	private final String secure_key = "79639ec221ba0c958eef5d66a7f8fdaa";
	private Api_Client api = new Api_Client( apk_key, secure_key );
	
	private BithumbMyBalanceInfo 	balanceInfo 		= new BithumbMyBalanceInfo(); 
	//private BithumbMyTicker tickerInfo = new BithumbMyTicker();
	private BithumbLastTicker 		requestLastTicker 	= new BithumbLastTicker();
	private BithumbMyTransactions 	requestTransaction 	= new BithumbMyTransactions();
	//private BithumbOrderBook  requestOrderBook 	= new BithumbOrderBook();
	
	private ObservableList<PriceData> priceTableDatas;
	private boolean threadFlag = true;
	private int lastPrices [] = null;
	private int lastBuyPrices [] = new int[ CURRENCY_DEF.MAX_CURRENCY ];
	private int lastSellPrices [] = new int[ CURRENCY_DEF.MAX_CURRENCY ];
	
	@FXML
	private TableView<PriceData> tvPriceTable;
	
	@FXML
	private Label lbTotalCache;
	
	@FXML
	private Label lbKrwCache;
		
	@FXML
	private LineChart<NumberAxis, NumberAxis> lcLineChart;
	
	@SuppressWarnings("rawtypes")
	//Series series [] 		= new Series[ CURRENCY_DEF.MAX_CURRENCY ];
	Series seriesFiveSec [] = new Series[ CURRENCY_DEF.MAX_CURRENCY ];
	List<Queue<Integer>> fiveSecValue = new ArrayList<Queue<Integer>>(); 
	
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
	
		TableColumn<PriceData, String> tcCurrencyLastPrice = (TableColumn<PriceData, String>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyLastPrice.setCellValueFactory( new PropertyValueFactory<PriceData, String>( "currencyLastPriceFormatString" ));
		tcCurrencyLastPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
		
		TableColumn<PriceData, String> tcCurrencyPrice = (TableColumn<PriceData, String>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyPrice.setCellValueFactory( new PropertyValueFactory<PriceData, String>( "currencyPriceFormatString" ));
		tcCurrencyPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
		
		TableColumn<PriceData, String> tcCurrencyLastBuyPrice = (TableColumn<PriceData, String>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyLastBuyPrice.setCellValueFactory( new PropertyValueFactory<PriceData, String>( "currencyLastBuyPriceFormatString" ));
		tcCurrencyLastBuyPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
		
		TableColumn<PriceData, String> tcCurrencyLastSellPrice = (TableColumn<PriceData, String>) tvPriceTable.getColumns().get(column_index++);
		tcCurrencyLastSellPrice.setCellValueFactory( new PropertyValueFactory<PriceData, String>( "currencyLastSellPriceFormatString" ));
		tcCurrencyLastSellPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
		
		priceTableDatas = FXCollections.observableArrayList( new ArrayList<PriceData>() ); 
		tvPriceTable.setItems( priceTableDatas );
		
		for( int i = 0; i < CURRENCY_DEF.MAX_CURRENCY; i++ ) {
			//series [i] 		 = new Series();
			seriesFiveSec[i] = new Series();
			fiveSecValue.add( new LinkedList<Integer>());
		}
		//lcLineChart.getData().add( series[ 			selectedCurrency ] );
		lcLineChart.getData().add( seriesFiveSec[ 	selectedCurrency ] );
				
		new Thread( traderBotThreadRunnable ).start();
	}
		
	private void changedMyTransactions() {
		balanceInfo.requestAPI( api );
		
		// 각 코인별 마지막 구매 금액을 설정한다.
		for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
			requestTransaction.setCurrency( idxCurrency );
			if( requestTransaction.requestAPI(api)) {
				lastBuyPrices	[ idxCurrency ] = requestTransaction.getLastBuyPrice();
				lastSellPrices	[ idxCurrency ] = requestTransaction.getLastSellPrice();
			}
		}
		
		Platform.runLater( new Runnable(){
			@Override
			public void run() {
				lbKrwCache.setText( CurrencyUtil.getIntegerToFormatString( balanceInfo.getKrw() ));
			}} );
	}
	
	Runnable traderBotThreadRunnable = new Runnable() {
		public void run() {
			changedMyTransactions();
						
			if( requestLastTicker.requestAPI( api )) {
				lastPrices 		= requestLastTicker.getLastPriceInfos();
			}
			
			// 10초간 데이터를 초기화 시킨다.  
			for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
				Queue<Integer> eachCurrencyFiveSecValues = fiveSecValue.get( idxCurrency );
				for( int i = 0; i < 10; i++ ) {
					eachCurrencyFiveSecValues.add( lastPrices[idxCurrency] );
				}				
			}
			
			// 실시간 거래 금액을 가지고 온다. 
			while( threadFlag ) {
				if( requestLastTicker.requestAPI( api )) {
					//System.out.println( requestLastTicker.toInfoString() );
					lastPrices 		= requestLastTicker.getLastPriceInfos();
				}
				Platform.runLater( uiUpdateRunnable );
				Util.sleepMillisecond( 500 );
			}
		}
	};
	
	SimpleDateFormat xTitleFormat = new SimpleDateFormat("HH:mm:ss");
	Runnable uiUpdateRunnable = new Runnable() {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void run() {
			priceTableDatas.clear();
			
			int nTempValue = 0;
			int myTotalCache = balanceInfo.getKrw();
			double balances[] = balanceInfo.getBalances();
			
			String strXTitle = "";
			for( int idxCurrency = 0; idxCurrency < CURRENCY_DEF.MAX_CURRENCY; idxCurrency++  ) {
				int cache = (int)( balances[ idxCurrency ] * lastPrices[ idxCurrency ]);
				
				PriceData priceData = new PriceData();
				priceData.setCurrencyName( CURRENCY_DEF.strCurrencies[ idxCurrency ] );
				priceData.setCurrencyUnits( balances[ idxCurrency ] );
				priceData.setCurrencyLastPrice( lastPrices[ idxCurrency ] );
				priceData.setCurrencyPrice( cache );
				priceData.setCurrencyLastBuyPrice( lastBuyPrices[ idxCurrency ] );
				priceData.setCurrencyLastSellPrice( lastSellPrices[ idxCurrency ] );
				
				myTotalCache += cache;
				
				strXTitle = xTitleFormat.format( new Date());
				priceTableDatas.add( priceData );
				//XYChart.Data newData = new XYChart.Data( strXTitle, lastPrices[ idxCurrency ] );					
				//series[idxCurrency].getData().add( newData );
				
				nTempValue = 0;
				Queue<Integer> eachCurrencyFiveSecValues = fiveSecValue.get( idxCurrency );
				for( Integer eachPrice : eachCurrencyFiveSecValues ) {
					nTempValue += eachPrice;					
				}
				XYChart.Data fiveSecNewData = new XYChart.Data( strXTitle, nTempValue / eachCurrencyFiveSecValues.size()  );
				seriesFiveSec[idxCurrency].getData().add( fiveSecNewData );
				
				eachCurrencyFiveSecValues.poll();
				eachCurrencyFiveSecValues.add( lastPrices[ idxCurrency ] );
			}
			
			lbTotalCache.setText( CurrencyUtil.getIntegerToFormatString( myTotalCache ));
		}
	};
		
	@FXML
	private void event_handle_mouse(MouseEvent e) {
		Control ctrl = ( Control ) e.getSource();
		
		switch( ctrl.getId()) {
		case "tvPriceTable" 				: mouse_handler_table_view(e, ctrl); break;
		}		
	}
	
	@FXML
	private void action_event_handler( ActionEvent e) {
		Object obj = e.getSource();
		
		String ctrl_id = null;
		if( obj instanceof Button) 			ctrl_id = (( Button ) obj ).getId();
		else if( obj instanceof MenuItem ) 	ctrl_id = (( MenuItem ) obj).getId();
				
		if( ctrl_id == null ) return;
		
		switch( ctrl_id ) {
		case "btnMarketBuy" 	: // 시장가 구매
			BithumbMarketBuy marketBuy = new BithumbMarketBuy();
			if( marketBuy.checkEnableOrder( selectedCurrency, requestLastTicker, balanceInfo)) {
				if( marketBuy.requestAPI(api)) {
					changedMyTransactions();
				}
			} else {
				String errMsg = String.format( "%d, %d, %.4f", balanceInfo.getKrw(), requestLastTicker.getLastMinSellPrice()[selectedCurrency], CURRENCY_DEF.minUnits[selectedCurrency] );
				Alert alert = new Alert(AlertType.NONE, String.format( "구매 금액이 부족하여 주문에 실패하였습니다.\n(%s)", errMsg ), ButtonType.OK );
				alert.showAndWait();
			}
			break;
			
		case "btnMarketSell" 	: // 시장가 판매
			BithumbMarketSell marketSell = new BithumbMarketSell();
			if( marketSell.checkEnableOrder( selectedCurrency, balanceInfo)) {
				api.setLogEnabled( true );
				if( marketSell.requestAPI(api)) {
					changedMyTransactions();
				}
				api.setLogEnabled( false );
			} else {
				Alert alert = new Alert(AlertType.NONE, String.format( "판매할 %s 코인이 부족하여 주문에 실패하였습니다.", CURRENCY_DEF.strCurrenciesKOR[selectedCurrency] ), ButtonType.OK );
				alert.showAndWait();
			}
			break;
		}
	}
	
	int selectedCurrency = CURRENCY_DEF.BTC;
	private void mouse_handler_table_view(MouseEvent e, Control ctrl) {
		selectedCurrency = tvPriceTable.getSelectionModel().getSelectedIndex();
		if( selectedCurrency < 0 || selectedCurrency > CURRENCY_DEF.MAX_CURRENCY ) return;
		
		lcLineChart.getData().clear();
		//lcLineChart.getData().add( series		[ selectedCurrency ] );
		lcLineChart.getData().add( seriesFiveSec[ selectedCurrency ] );
				
		lcLineChart.setTitle( CURRENCY_DEF.strCurrenciesKOR[ selectedCurrency ]  );
		
		requestTransaction.setCurrency( selectedCurrency );
		requestTransaction.requestAPI(api);			
	}
}

