.class public final Lcom/google/android/gms/games/Games$GamesOptions;
.super Ljava/lang/Object;

# interfaces
.implements Lcom/google/android/gms/common/api/Api$ApiOptions$Optional;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/android/gms/games/Games;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "GamesOptions"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/google/android/gms/games/Games$GamesOptions$Builder;
    }
.end annotation


# instance fields
.field public final Xa:Z

.field public final Xb:Z

.field public final Xc:I

.field public final Xd:Z

.field public final Xe:I

.field public final Xf:Ljava/lang/String;

.field public final Xg:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method private constructor <init>()V
    .locals 2

    const/4 v1, 0x0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-boolean v1, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xa:Z

    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xb:Z

    const/16 v0, 0x11

    iput v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xc:I

    iput-boolean v1, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xd:Z

    const/16 v0, 0x1110

    iput v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xe:I

    const/4 v0, 0x0

    iput-object v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xf:Ljava/lang/String;

    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xg:Ljava/util/ArrayList;

    return-void
.end method

.method synthetic constructor <init>(Lcom/google/android/gms/games/Games$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/google/android/gms/games/Games$1;

    .prologue
    invoke-direct {p0}, Lcom/google/android/gms/games/Games$GamesOptions;-><init>()V

    return-void
.end method

.method private constructor <init>(Lcom/google/android/gms/games/Games$GamesOptions$Builder;)V
    .locals 1
    .param p1, "builder"    # Lcom/google/android/gms/games/Games$GamesOptions$Builder;

    .prologue
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iget-boolean v0, p1, Lcom/google/android/gms/games/Games$GamesOptions$Builder;->Xa:Z

    iput-boolean v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xa:Z

    iget-boolean v0, p1, Lcom/google/android/gms/games/Games$GamesOptions$Builder;->Xb:Z

    iput-boolean v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xb:Z

    iget v0, p1, Lcom/google/android/gms/games/Games$GamesOptions$Builder;->Xc:I

    iput v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xc:I

    iget-boolean v0, p1, Lcom/google/android/gms/games/Games$GamesOptions$Builder;->Xd:Z

    iput-boolean v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xd:Z

    iget v0, p1, Lcom/google/android/gms/games/Games$GamesOptions$Builder;->Xe:I

    iput v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xe:I

    iget-object v0, p1, Lcom/google/android/gms/games/Games$GamesOptions$Builder;->Xf:Ljava/lang/String;

    iput-object v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xf:Ljava/lang/String;

    iget-object v0, p1, Lcom/google/android/gms/games/Games$GamesOptions$Builder;->Xg:Ljava/util/ArrayList;

    iput-object v0, p0, Lcom/google/android/gms/games/Games$GamesOptions;->Xg:Ljava/util/ArrayList;

    return-void
.end method

.method synthetic constructor <init>(Lcom/google/android/gms/games/Games$GamesOptions$Builder;Lcom/google/android/gms/games/Games$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/google/android/gms/games/Games$GamesOptions$Builder;
    .param p2, "x1"    # Lcom/google/android/gms/games/Games$1;

    .prologue
    invoke-direct {p0, p1}, Lcom/google/android/gms/games/Games$GamesOptions;-><init>(Lcom/google/android/gms/games/Games$GamesOptions$Builder;)V

    return-void
.end method

.method public static builder()Lcom/google/android/gms/games/Games$GamesOptions$Builder;
    .locals 2

    new-instance v0, Lcom/google/android/gms/games/Games$GamesOptions$Builder;

    const/4 v1, 0x0

    invoke-direct {v0, v1}, Lcom/google/android/gms/games/Games$GamesOptions$Builder;-><init>(Lcom/google/android/gms/games/Games$1;)V

    return-object v0
.end method
