.class public interface abstract Lcom/google/android/gms/games/appcontent/AppContentAction;
.super Ljava/lang/Object;

# interfaces
.implements Landroid/os/Parcelable;
.implements Lcom/google/android/gms/common/data/Freezable;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Landroid/os/Parcelable;",
        "Lcom/google/android/gms/common/data/Freezable",
        "<",
        "Lcom/google/android/gms/games/appcontent/AppContentAction;",
        ">;"
    }
.end annotation


# virtual methods
.method public abstract getExtras()Landroid/os/Bundle;
.end method

.method public abstract getLabel()Ljava/lang/String;
.end method

.method public abstract getType()Ljava/lang/String;
.end method

.method public abstract kH()Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List",
            "<",
            "Lcom/google/android/gms/games/appcontent/AppContentCondition;",
            ">;"
        }
    .end annotation
.end method

.method public abstract kI()Ljava/lang/String;
.end method

.method public abstract kJ()Ljava/lang/String;
.end method
