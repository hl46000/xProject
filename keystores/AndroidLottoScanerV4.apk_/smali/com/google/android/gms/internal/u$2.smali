.class Lcom/google/android/gms/internal/u$2;
.super Ljava/lang/Object;

# interfaces
.implements Landroid/view/View$OnTouchListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/google/android/gms/internal/u;->a(Lcom/google/android/gms/internal/fy$a;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic lG:Lcom/google/android/gms/internal/u;

.field final synthetic lH:Lcom/google/android/gms/internal/v;


# direct methods
.method constructor <init>(Lcom/google/android/gms/internal/u;Lcom/google/android/gms/internal/v;)V
    .locals 0

    iput-object p1, p0, Lcom/google/android/gms/internal/u$2;->lG:Lcom/google/android/gms/internal/u;

    iput-object p2, p0, Lcom/google/android/gms/internal/u$2;->lH:Lcom/google/android/gms/internal/v;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 1
    .param p1, "v"    # Landroid/view/View;
    .param p2, "event"    # Landroid/view/MotionEvent;

    .prologue
    iget-object v0, p0, Lcom/google/android/gms/internal/u$2;->lH:Lcom/google/android/gms/internal/v;

    invoke-virtual {v0}, Lcom/google/android/gms/internal/v;->av()V

    const/4 v0, 0x0

    return v0
.end method
