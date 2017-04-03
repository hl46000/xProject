.class public abstract Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;
.super Ljava/lang/Object;
.source "SupplementalInfoRetriever.java"

# interfaces
.implements Ljava/util/concurrent/Callable;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljava/util/concurrent/Callable",
        "<",
        "Ljava/lang/Void;",
        ">;"
    }
.end annotation


# instance fields
.field private final context:Landroid/content/Context;

.field private final handler:Landroid/os/Handler;

.field private final textViewRef:Ljava/lang/ref/WeakReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/ref/WeakReference",
            "<",
            "Landroid/widget/TextView;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method constructor <init>(Landroid/widget/TextView;Landroid/os/Handler;Landroid/content/Context;)V
    .locals 1
    .param p1, "textView"    # Landroid/widget/TextView;
    .param p2, "handler"    # Landroid/os/Handler;
    .param p3, "context"    # Landroid/content/Context;

    .prologue
    .line 38
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 39
    new-instance v0, Ljava/lang/ref/WeakReference;

    invoke-direct {v0, p1}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    iput-object v0, p0, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->textViewRef:Ljava/lang/ref/WeakReference;

    .line 40
    iput-object p2, p0, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->handler:Landroid/os/Handler;

    .line 41
    iput-object p3, p0, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->context:Landroid/content/Context;

    .line 42
    return-void
.end method

.method static synthetic access$1(Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;)Landroid/content/Context;
    .locals 1

    .prologue
    .line 36
    iget-object v0, p0, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->context:Landroid/content/Context;

    return-object v0
.end method


# virtual methods
.method final append(Ljava/lang/String;)V
    .locals 3
    .param p1, "newText"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/InterruptedException;
        }
    .end annotation

    .prologue
    .line 52
    iget-object v1, p0, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->textViewRef:Ljava/lang/ref/WeakReference;

    invoke-virtual {v1}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    .line 53
    .local v0, "textView":Landroid/widget/TextView;
    if-nez v0, :cond_0

    .line 54
    new-instance v1, Ljava/lang/InterruptedException;

    invoke-direct {v1}, Ljava/lang/InterruptedException;-><init>()V

    throw v1

    .line 56
    :cond_0
    iget-object v1, p0, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->handler:Landroid/os/Handler;

    new-instance v2, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever$1;

    invoke-direct {v2, p0, p1, v0}, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever$1;-><init>(Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;Ljava/lang/String;Landroid/widget/TextView;)V

    invoke-virtual {v1, v2}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 62
    return-void
.end method

.method public bridge synthetic call()Ljava/lang/Object;
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 1
    invoke-virtual {p0}, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->call()Ljava/lang/Void;

    move-result-object v0

    return-object v0
.end method

.method public final call()Ljava/lang/Void;
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;,
            Ljava/lang/InterruptedException;
        }
    .end annotation

    .prologue
    .line 45
    invoke-virtual {p0}, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->retrieveSupplementalInfo()V

    .line 46
    const/4 v0, 0x0

    return-object v0
.end method

.method abstract retrieveSupplementalInfo()V
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;,
            Ljava/lang/InterruptedException;
        }
    .end annotation
.end method

.method final setLink(Ljava/lang/String;)V
    .locals 2
    .param p1, "uri"    # Ljava/lang/String;

    .prologue
    .line 65
    iget-object v0, p0, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;->textViewRef:Ljava/lang/ref/WeakReference;

    invoke-virtual {v0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/widget/TextView;

    new-instance v1, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever$2;

    invoke-direct {v1, p0, p1}, Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever$2;-><init>(Lcom/google/zxing/client/android/result/supplement/SupplementalInfoRetriever;Ljava/lang/String;)V

    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 72
    return-void
.end method
