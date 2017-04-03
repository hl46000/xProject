.class public Lat/markushi/ui/action/PlusAction;
.super Lat/markushi/ui/action/Action;
.source "PlusAction.java"


# direct methods
.method public constructor <init>()V
    .locals 6

    .prologue
    .line 5
    invoke-direct {p0}, Lat/markushi/ui/action/Action;-><init>()V

    .line 7
    const v0, 0x3f4aaaab

    .line 8
    .local v0, "bottom":F
    const v4, 0x3e555554

    .line 9
    .local v4, "top":F
    const v2, 0x3e555555

    .line 10
    .local v2, "left":F
    const v3, 0x3f4aaaab

    .line 11
    .local v3, "right":F
    const/high16 v1, 0x3f000000    # 0.5f

    .line 13
    .local v1, "center":F
    const/16 v5, 0xc

    new-array v5, v5, [F

    fill-array-data v5, :array_0

    iput-object v5, p0, Lat/markushi/ui/action/PlusAction;->lineData:[F

    .line 20
    return-void

    .line 13
    nop

    :array_0
    .array-data 4
        0x3f000000    # 0.5f
        0x3e555554
        0x3f000000    # 0.5f
        0x3f4aaaab
        0x3e555555
        0x3f000000    # 0.5f
        0x3f4aaaab
        0x3f000000    # 0.5f
        0x3f000000    # 0.5f
        0x3e555554
        0x3f000000    # 0.5f
        0x3f4aaaab
    .end array-data
.end method
