.class public Lcom/purehero/lotto/scan/MainActivity;
.super Landroid/support/v7/app/ActionBarActivity;
.source "MainActivity.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# instance fields
.field private final BACK_PRESSED_TIME_INTERVAL:I

.field private backPressedTime:J

.field private bannerAdView:Lcom/google/android/gms/ads/AdView;

.field private interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

.field private mWebView:Landroid/webkit/WebView;

.field private progressBar:Landroid/widget/ProgressBar;


# direct methods
.method public constructor <init>()V
    .locals 2

    .prologue
    const/4 v0, 0x0

    .line 26
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;-><init>()V

    .line 28
    iput-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->bannerAdView:Lcom/google/android/gms/ads/AdView;

    .line 29
    iput-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    .line 31
    iput-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->progressBar:Landroid/widget/ProgressBar;

    .line 32
    iput-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    .line 159
    const/16 v0, 0x7d0

    iput v0, p0, Lcom/purehero/lotto/scan/MainActivity;->BACK_PRESSED_TIME_INTERVAL:I

    .line 160
    const-wide/16 v0, 0x0

    iput-wide v0, p0, Lcom/purehero/lotto/scan/MainActivity;->backPressedTime:J

    .line 26
    return-void
.end method

.method static synthetic access$0(Lcom/purehero/lotto/scan/MainActivity;)Lcom/google/android/gms/ads/AdView;
    .locals 1

    .prologue
    .line 28
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->bannerAdView:Lcom/google/android/gms/ads/AdView;

    return-object v0
.end method

.method static synthetic access$1(Lcom/purehero/lotto/scan/MainActivity;)Landroid/widget/ProgressBar;
    .locals 1

    .prologue
    .line 31
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->progressBar:Landroid/widget/ProgressBar;

    return-object v0
.end method

.method static synthetic access$2(Lcom/purehero/lotto/scan/MainActivity;)Lcom/google/android/gms/ads/InterstitialAd;
    .locals 1

    .prologue
    .line 29
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    return-object v0
.end method

.method private init_webview()V
    .locals 3

    .prologue
    .line 92
    const v1, 0x7f090062

    invoke-virtual {p0, v1}, Lcom/purehero/lotto/scan/MainActivity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/webkit/WebView;

    iput-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    .line 93
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    if-nez v1, :cond_0

    .line 125
    :goto_0
    return-void

    .line 95
    :cond_0
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    invoke-virtual {v1}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;

    move-result-object v0

    .line 96
    .local v0, "webSettings":Landroid/webkit/WebSettings;
    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Landroid/webkit/WebSettings;->setJavaScriptEnabled(Z)V

    .line 98
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    new-instance v2, Lcom/purehero/lotto/scan/MainActivity$2;

    invoke-direct {v2, p0}, Lcom/purehero/lotto/scan/MainActivity$2;-><init>(Lcom/purehero/lotto/scan/MainActivity;)V

    invoke-virtual {v1, v2}, Landroid/webkit/WebView;->setWebViewClient(Landroid/webkit/WebViewClient;)V

    goto :goto_0
.end method

.method private openUrl(Ljava/lang/String;)V
    .locals 2
    .param p1, "url"    # Ljava/lang/String;

    .prologue
    .line 128
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    if-nez v0, :cond_0

    .line 135
    :goto_0
    return-void

    .line 130
    :cond_0
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->progressBar:Landroid/widget/ProgressBar;

    if-eqz v0, :cond_1

    .line 131
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->progressBar:Landroid/widget/ProgressBar;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/ProgressBar;->setVisibility(I)V

    .line 134
    :cond_1
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    invoke-virtual {v0, p1}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V

    goto :goto_0
.end method


# virtual methods
.method protected onActivityResult(IILandroid/content/Intent;)V
    .locals 5
    .param p1, "arg0"    # I
    .param p2, "arg1"    # I
    .param p3, "arg2"    # Landroid/content/Intent;

    .prologue
    const/4 v4, 0x0

    .line 74
    invoke-super {p0, p1, p2, p3}, Landroid/support/v7/app/ActionBarActivity;->onActivityResult(IILandroid/content/Intent;)V

    .line 76
    if-eqz p3, :cond_0

    .line 77
    const-string v1, "CONTENTS"

    invoke-virtual {p3, v1}, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 78
    .local v0, "contents":Ljava/lang/String;
    if-nez v0, :cond_1

    .line 79
    const-string v1, "LottoScaner"

    const-string v2, "Activity Result NULL"

    new-array v3, v4, [Ljava/lang/Object;

    invoke-static {v2, v3}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 87
    .end local v0    # "contents":Ljava/lang/String;
    :cond_0
    :goto_0
    invoke-virtual {p0}, Lcom/purehero/lotto/scan/MainActivity;->showFullAd()V

    .line 88
    return-void

    .line 82
    .restart local v0    # "contents":Ljava/lang/String;
    :cond_1
    invoke-direct {p0, v0}, Lcom/purehero/lotto/scan/MainActivity;->openUrl(Ljava/lang/String;)V

    .line 83
    const-string v1, "LottoScaner"

    const-string v2, "Activity Result : %s"

    const/4 v3, 0x1

    new-array v3, v3, [Ljava/lang/Object;

    aput-object v0, v3, v4

    invoke-static {v2, v3}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_0
.end method

.method public onBackPressed()V
    .locals 6

    .prologue
    .line 164
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    invoke-virtual {v1}, Landroid/webkit/WebView;->copyBackForwardList()Landroid/webkit/WebBackForwardList;

    move-result-object v0

    .line 166
    .local v0, "list":Landroid/webkit/WebBackForwardList;
    invoke-virtual {v0}, Landroid/webkit/WebBackForwardList;->getCurrentIndex()I

    move-result v1

    if-lez v1, :cond_0

    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    invoke-virtual {v1}, Landroid/webkit/WebView;->canGoBack()Z

    move-result v1

    if-eqz v1, :cond_0

    .line 167
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->mWebView:Landroid/webkit/WebView;

    const/4 v2, -0x1

    invoke-virtual {v1, v2}, Landroid/webkit/WebView;->goBackOrForward(I)V

    .line 178
    :goto_0
    return-void

    .line 170
    :cond_0
    iget-wide v2, p0, Lcom/purehero/lotto/scan/MainActivity;->backPressedTime:J

    const-wide/16 v4, 0x7d0

    add-long/2addr v2, v4

    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v4

    cmp-long v1, v2, v4

    if-lez v1, :cond_1

    .line 171
    invoke-super {p0}, Landroid/support/v7/app/ActionBarActivity;->onBackPressed()V

    goto :goto_0

    .line 174
    :cond_1
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v2

    iput-wide v2, p0, Lcom/purehero/lotto/scan/MainActivity;->backPressedTime:J

    .line 175
    const v1, 0x7f0c003a

    const/4 v2, 0x0

    invoke-static {p0, v1, v2}, Landroid/widget/Toast;->makeText(Landroid/content/Context;II)Landroid/widget/Toast;

    move-result-object v1

    invoke-virtual {v1}, Landroid/widget/Toast;->show()V

    goto :goto_0
.end method

.method public onClick(Landroid/view/View;)V
    .locals 0
    .param p1, "arg0"    # Landroid/view/View;

    .prologue
    .line 217
    invoke-virtual {p1}, Landroid/view/View;->getId()I

    .line 222
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .prologue
    .line 36
    invoke-super {p0, p1}, Landroid/support/v7/app/ActionBarActivity;->onCreate(Landroid/os/Bundle;)V

    .line 37
    const v1, 0x7f030018

    invoke-virtual {p0, v1}, Lcom/purehero/lotto/scan/MainActivity;->setContentView(I)V

    .line 39
    const v1, 0x7f090063

    invoke-virtual {p0, v1}, Lcom/purehero/lotto/scan/MainActivity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Landroid/widget/ProgressBar;

    iput-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->progressBar:Landroid/widget/ProgressBar;

    .line 53
    const v1, 0x7f090061

    invoke-virtual {p0, v1}, Lcom/purehero/lotto/scan/MainActivity;->findViewById(I)Landroid/view/View;

    move-result-object v1

    check-cast v1, Lcom/google/android/gms/ads/AdView;

    iput-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->bannerAdView:Lcom/google/android/gms/ads/AdView;

    .line 54
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->bannerAdView:Lcom/google/android/gms/ads/AdView;

    if-eqz v1, :cond_0

    .line 55
    new-instance v1, Lcom/google/android/gms/ads/AdRequest$Builder;

    invoke-direct {v1}, Lcom/google/android/gms/ads/AdRequest$Builder;-><init>()V

    invoke-virtual {v1}, Lcom/google/android/gms/ads/AdRequest$Builder;->build()Lcom/google/android/gms/ads/AdRequest;

    move-result-object v0

    .line 56
    .local v0, "adRequest":Lcom/google/android/gms/ads/AdRequest;
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->bannerAdView:Lcom/google/android/gms/ads/AdView;

    new-instance v2, Lcom/purehero/lotto/scan/MainActivity$1;

    invoke-direct {v2, p0}, Lcom/purehero/lotto/scan/MainActivity$1;-><init>(Lcom/purehero/lotto/scan/MainActivity;)V

    invoke-virtual {v1, v2}, Lcom/google/android/gms/ads/AdView;->setAdListener(Lcom/google/android/gms/ads/AdListener;)V

    .line 63
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->bannerAdView:Lcom/google/android/gms/ads/AdView;

    invoke-virtual {v1, v0}, Lcom/google/android/gms/ads/AdView;->loadAd(Lcom/google/android/gms/ads/AdRequest;)V

    .line 66
    .end local v0    # "adRequest":Lcom/google/android/gms/ads/AdRequest;
    :cond_0
    invoke-direct {p0}, Lcom/purehero/lotto/scan/MainActivity;->init_webview()V

    .line 67
    const-string v1, "http://m.nlotto.co.kr/"

    invoke-direct {p0, v1}, Lcom/purehero/lotto/scan/MainActivity;->openUrl(Ljava/lang/String;)V

    .line 68
    return-void
.end method

.method public onCreateOptionsMenu(Landroid/view/Menu;)Z
    .locals 2
    .param p1, "menu"    # Landroid/view/Menu;

    .prologue
    .line 140
    invoke-virtual {p0}, Lcom/purehero/lotto/scan/MainActivity;->getMenuInflater()Landroid/view/MenuInflater;

    move-result-object v0

    const/high16 v1, 0x7f0f0000

    invoke-virtual {v0, v1, p1}, Landroid/view/MenuInflater;->inflate(ILandroid/view/Menu;)V

    .line 141
    const/4 v0, 0x1

    return v0
.end method

.method public onOptionsItemSelected(Landroid/view/MenuItem;)Z
    .locals 3
    .param p1, "item"    # Landroid/view/MenuItem;

    .prologue
    .line 149
    invoke-interface {p1}, Landroid/view/MenuItem;->getItemId()I

    move-result v0

    .line 150
    .local v0, "id":I
    const v1, 0x7f09006c

    if-ne v0, v1, :cond_0

    .line 151
    new-instance v1, Landroid/content/Intent;

    const-class v2, Lcom/google/zxing/client/android/CaptureActivity;

    invoke-direct {v1, p0, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    const/16 v2, 0x64

    invoke-virtual {p0, v1, v2}, Lcom/purehero/lotto/scan/MainActivity;->startActivityForResult(Landroid/content/Intent;I)V

    .line 152
    const/4 v1, 0x1

    .line 155
    :goto_0
    return v1

    :cond_0
    invoke-super {p0, p1}, Landroid/support/v7/app/ActionBarActivity;->onOptionsItemSelected(Landroid/view/MenuItem;)Z

    move-result v1

    goto :goto_0
.end method

.method public showFullAd()V
    .locals 6

    .prologue
    .line 181
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    if-eqz v1, :cond_1

    .line 182
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    invoke-virtual {v1}, Lcom/google/android/gms/ads/InterstitialAd;->isLoaded()Z

    move-result v1

    if-eqz v1, :cond_0

    .line 183
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v2

    const-wide/16 v4, 0xa

    rem-long/2addr v2, v4

    const-wide/16 v4, 0x3

    cmp-long v1, v2, v4

    if-gez v1, :cond_0

    .line 185
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    invoke-virtual {v1}, Lcom/google/android/gms/ads/InterstitialAd;->show()V

    .line 211
    :cond_0
    :goto_0
    return-void

    .line 191
    :cond_1
    new-instance v1, Lcom/google/android/gms/ads/InterstitialAd;

    invoke-direct {v1, p0}, Lcom/google/android/gms/ads/InterstitialAd;-><init>(Landroid/content/Context;)V

    iput-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    .line 192
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    invoke-virtual {p0}, Lcom/purehero/lotto/scan/MainActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    const v3, 0x7f0c0039

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Lcom/google/android/gms/ads/InterstitialAd;->setAdUnitId(Ljava/lang/String;)V

    .line 193
    new-instance v1, Lcom/google/android/gms/ads/AdRequest$Builder;

    invoke-direct {v1}, Lcom/google/android/gms/ads/AdRequest$Builder;-><init>()V

    invoke-virtual {v1}, Lcom/google/android/gms/ads/AdRequest$Builder;->build()Lcom/google/android/gms/ads/AdRequest;

    move-result-object v0

    .line 194
    .local v0, "adRequest":Lcom/google/android/gms/ads/AdRequest;
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    invoke-virtual {v1, v0}, Lcom/google/android/gms/ads/InterstitialAd;->loadAd(Lcom/google/android/gms/ads/AdRequest;)V

    .line 195
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;

    new-instance v2, Lcom/purehero/lotto/scan/MainActivity$3;

    invoke-direct {v2, p0}, Lcom/purehero/lotto/scan/MainActivity$3;-><init>(Lcom/purehero/lotto/scan/MainActivity;)V

    invoke-virtual {v1, v2}, Lcom/google/android/gms/ads/InterstitialAd;->setAdListener(Lcom/google/android/gms/ads/AdListener;)V

    goto :goto_0
.end method
