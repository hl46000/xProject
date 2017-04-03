.class Lcom/purehero/lotto/scan/MainActivity$1;
.super Lcom/google/android/gms/ads/AdListener;
.source "MainActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/purehero/lotto/scan/MainActivity;->onCreate(Landroid/os/Bundle;)V
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
    iput-object p1, p0, Lcom/purehero/lotto/scan/MainActivity$1;->this$0:Lcom/purehero/lotto/scan/MainActivity;

    .line 56
    invoke-direct {p0}, Lcom/google/android/gms/ads/AdListener;-><init>()V

    return-void
.end method


# virtual methods
.method public onAdLoaded()V
    .locals 2

    .prologue
    .line 59
    invoke-super {p0}, Lcom/google/android/gms/ads/AdListener;->onAdLoaded()V

    .line 60
    iget-object v0, p0, Lcom/purehero/lotto/scan/MainActivity$1;->this$0:Lcom/purehero/lotto/scan/MainActivity;

    # getter for: Lcom/purehero/lotto/scan/MainActivity;->bannerAdView:Lcom/google/android/gms/ads/AdView;
    invoke-static {v0}, Lcom/purehero/lotto/scan/MainActivity;->access$0(Lcom/purehero/lotto/scan/MainActivity;)Lcom/google/android/gms/ads/AdView;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Lcom/google/android/gms/ads/AdView;->setVisibility(I)V

    .line 61
    return-void
.end method
