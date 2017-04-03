.class final Lcom/google/zxing/client/android/InactivityTimer;
.super Ljava/lang/Object;
.source "InactivityTimer.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/google/zxing/client/android/InactivityTimer$DaemonThreadFactory;,
        Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;
    }
.end annotation


# static fields
.field private static final INACTIVITY_DELAY_SECONDS:I = 0x12c


# instance fields
.field private final activity:Landroid/app/Activity;

.field private inactivityFuture:Ljava/util/concurrent/ScheduledFuture;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/ScheduledFuture",
            "<*>;"
        }
    .end annotation
.end field

.field private final inactivityTimer:Ljava/util/concurrent/ScheduledExecutorService;

.field private final powerStatusReceiver:Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;


# direct methods
.method constructor <init>(Landroid/app/Activity;)V
    .locals 2
    .param p1, "activity"    # Landroid/app/Activity;

    .prologue
    const/4 v1, 0x0

    .line 44
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 39
    new-instance v0, Lcom/google/zxing/client/android/InactivityTimer$DaemonThreadFactory;

    invoke-direct {v0, v1}, Lcom/google/zxing/client/android/InactivityTimer$DaemonThreadFactory;-><init>(Lcom/google/zxing/client/android/InactivityTimer$DaemonThreadFactory;)V

    invoke-static {v0}, Ljava/util/concurrent/Executors;->newSingleThreadScheduledExecutor(Ljava/util/concurrent/ThreadFactory;)Ljava/util/concurrent/ScheduledExecutorService;

    move-result-object v0

    iput-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityTimer:Ljava/util/concurrent/ScheduledExecutorService;

    .line 41
    iput-object v1, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityFuture:Ljava/util/concurrent/ScheduledFuture;

    .line 42
    new-instance v0, Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;

    invoke-direct {v0, p0, v1}, Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;-><init>(Lcom/google/zxing/client/android/InactivityTimer;Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;)V

    iput-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->powerStatusReceiver:Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;

    .line 45
    iput-object p1, p0, Lcom/google/zxing/client/android/InactivityTimer;->activity:Landroid/app/Activity;

    .line 46
    invoke-virtual {p0}, Lcom/google/zxing/client/android/InactivityTimer;->onActivity()V

    .line 47
    return-void
.end method

.method static synthetic access$0(Lcom/google/zxing/client/android/InactivityTimer;)V
    .locals 0

    .prologue
    .line 64
    invoke-direct {p0}, Lcom/google/zxing/client/android/InactivityTimer;->cancel()V

    return-void
.end method

.method private cancel()V
    .locals 2

    .prologue
    .line 65
    iget-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityFuture:Ljava/util/concurrent/ScheduledFuture;

    if-eqz v0, :cond_0

    .line 66
    iget-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityFuture:Ljava/util/concurrent/ScheduledFuture;

    const/4 v1, 0x1

    invoke-interface {v0, v1}, Ljava/util/concurrent/ScheduledFuture;->cancel(Z)Z

    .line 67
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityFuture:Ljava/util/concurrent/ScheduledFuture;

    .line 69
    :cond_0
    return-void
.end method


# virtual methods
.method onActivity()V
    .locals 5

    .prologue
    .line 50
    invoke-direct {p0}, Lcom/google/zxing/client/android/InactivityTimer;->cancel()V

    .line 51
    iget-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityTimer:Ljava/util/concurrent/ScheduledExecutorService;

    new-instance v1, Lcom/google/zxing/client/android/FinishListener;

    iget-object v2, p0, Lcom/google/zxing/client/android/InactivityTimer;->activity:Landroid/app/Activity;

    invoke-direct {v1, v2}, Lcom/google/zxing/client/android/FinishListener;-><init>(Landroid/app/Activity;)V

    .line 52
    const-wide/16 v2, 0x12c

    .line 53
    sget-object v4, Ljava/util/concurrent/TimeUnit;->SECONDS:Ljava/util/concurrent/TimeUnit;

    .line 51
    invoke-interface {v0, v1, v2, v3, v4}, Ljava/util/concurrent/ScheduledExecutorService;->schedule(Ljava/lang/Runnable;JLjava/util/concurrent/TimeUnit;)Ljava/util/concurrent/ScheduledFuture;

    move-result-object v0

    iput-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityFuture:Ljava/util/concurrent/ScheduledFuture;

    .line 54
    return-void
.end method

.method public onPause()V
    .locals 2

    .prologue
    .line 57
    iget-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->activity:Landroid/app/Activity;

    iget-object v1, p0, Lcom/google/zxing/client/android/InactivityTimer;->powerStatusReceiver:Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;

    invoke-virtual {v0, v1}, Landroid/app/Activity;->unregisterReceiver(Landroid/content/BroadcastReceiver;)V

    .line 58
    return-void
.end method

.method public onResume()V
    .locals 4

    .prologue
    .line 61
    iget-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->activity:Landroid/app/Activity;

    iget-object v1, p0, Lcom/google/zxing/client/android/InactivityTimer;->powerStatusReceiver:Lcom/google/zxing/client/android/InactivityTimer$PowerStatusReceiver;

    new-instance v2, Landroid/content/IntentFilter;

    const-string v3, "android.intent.action.BATTERY_CHANGED"

    invoke-direct {v2, v3}, Landroid/content/IntentFilter;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v1, v2}, Landroid/app/Activity;->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;

    .line 62
    return-void
.end method

.method shutdown()V
    .locals 1

    .prologue
    .line 72
    invoke-direct {p0}, Lcom/google/zxing/client/android/InactivityTimer;->cancel()V

    .line 73
    iget-object v0, p0, Lcom/google/zxing/client/android/InactivityTimer;->inactivityTimer:Ljava/util/concurrent/ScheduledExecutorService;

    invoke-interface {v0}, Ljava/util/concurrent/ScheduledExecutorService;->shutdown()V

    .line 74
    return-void
.end method
