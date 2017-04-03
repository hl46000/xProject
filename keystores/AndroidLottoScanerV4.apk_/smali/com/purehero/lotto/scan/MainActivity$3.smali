.class Lcom/purehero/lotto/scan/MainActivity$3;
.super Lcom/google/android/gms/ads/AdListener;
.source "MainActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/purehero/lotto/scan/MainActivity;->showFullAd()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/purehero/lotto/scan/MainActivity;


# direct methods
.method constructor <init>(Lcom/purehero/lotto/scan/MainActivity;)V
    .locals 0

    .prologue
    .line 1
    iput-object p1, p0, Lcom/purehero/lotto/scan/MainActivity$3;->this$0:Lcom/purehero/lotto/scan/MainActivity;

    .line 195
    invoke-direct {p0}, Lcom/google/android/gms/ads/AdListener;-><init>()V

    return-void
.end method


# virtual methods
.method public onAdClosed()V
    .locals 2

    .prologue
    .line 207
    new-instance v1, Lcom/google/android/gms/ads/AdRequest$Builder;

    invoke-direct {v1}, Lcom/google/android/gms/ads/AdRequest$Builder;-><init>()V

    invoke-virtual {v1}, Lcom/google/android/gms/ads/AdRequest$Builder;->build()Lcom/google/android/gms/ads/AdRequest;

    move-result-object v0

    .line 208
    .local v0, "adRequest":Lcom/google/android/gms/ads/AdRequest;
    iget-object v1, p0, Lcom/purehero/lotto/scan/MainActivity$3;->this$0:Lcom/purehero/lotto/scan/MainActivity;

    # getter for: Lcom/purehero/lotto/scan/MainActivity;->interstitialAd:Lcom/google/android/gms/ads/InterstitialAd;
    invoke-static {v1}, Lcom/purehero/lotto/scan/MainActivity;->access$2(Lcom/purehero/lotto/scan/MainActivity;)Lcom/google/android/gms/ads/InterstitialAd;

    move-result-object v1

    invoke-virtual {v1, v0}, Lcom/google/android/gms/ads/InterstitialAd;->loadAd(Lcom/google/android/gms/ads/AdRequest;)V

    .line 209
    return-void
.end method

.method public onAdFailedToLoad(I)V
    .locals 0
    .param p1, "errorCode"    # I

    .prologue
    .line 199
    return-void
.end method

.method public onAdLoaded()V
    .locals 0

    .prologue
    .line 203
    return-void
.end method
