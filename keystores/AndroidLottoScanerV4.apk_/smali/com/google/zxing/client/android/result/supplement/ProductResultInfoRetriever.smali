.class final Lcom/google/zxing/client/android/result/supplement/ProductResultInfoRetriever;
.super Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;
.source "ProductResultInfoRetriever.java"


# direct methods
.method constructor <init>(Landroid/widget/TextView;Ljava/lang/String;Landroid/os/Handler;Landroid/content/Context;)V
    .locals 0
    .param p1, "textView"    # Landroid/widget/TextView;
    .param p2, "productID"    # Ljava/lang/String;
    .param p3, "handler"    # Landroid/os/Handler;
    .param p4, "context"    # Landroid/content/Context;

    .prologue
    .line 44
    invoke-direct {p0, p1, p3, p4}, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;-><init>(Landroid/widget/TextView;Landroid/os/Handler;Landroid/content/Context;)V

    .line 46
    return-void
.end method


# virtual methods
.method retrieveSupplementalInfo()V
    .locals 0
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;,
            Ljava/lang/InterruptedException;
        }
    .end annotation

    .prologue
    .line 73
    return-void
.end method
