.class public Lat/markushi/ui/action/CloseAction;
.super Lat/markushi/ui/action/Action;
.source "CloseAction.java"


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    .line 5
    invoke-direct {p0}, Lat/markushi/ui/action/Action;-><init>()V

    .line 6
    const v1, 0x3e751eb8

    .line 7
    .local v1, "start":F
    const v0, 0x3f42b852

    .line 9
    .local v0, "end":F
    const/16 v2, 0xc

    new-array v2, v2, [F

    fill-array-data v2, :array_0

    iput-object v2, p0, Lat/markushi/ui/action/CloseAction;->lineData:[F

    .line 16
    return-void

    .line 9
    nop

    :array_0
    .array-data 4
        0x3e751eb8
        0x3e751eb8
        0x3f42b852
        0x3f42b852
        0x3e751eb8
        0x3f42b852
        0x3f42b852
        0x3e751eb8
        0x3e751eb8
        0x3e751eb8
        0x3f42b852
        0x3f42b852
    .end array-data
.end method
