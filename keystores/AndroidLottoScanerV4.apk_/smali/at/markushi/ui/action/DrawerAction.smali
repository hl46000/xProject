.class public Lat/markushi/ui/action/DrawerAction;
.super Lat/markushi/ui/action/Action;
.source "DrawerAction.java"


# direct methods
.method public constructor <init>()V
    .locals 6

    .prologue
    .line 5
    invoke-direct {p0}, Lat/markushi/ui/action/Action;-><init>()V

    .line 7
    const v3, 0x3e0ccccd    # 0.1375f

    .line 8
    .local v3, "startX":F
    const v1, 0x3f5ccccd    # 0.8625f

    .line 9
    .local v1, "endX":F
    const v2, 0x3f34fdf4    # 0.707f

    .line 10
    .local v2, "endY":F
    const v4, 0x3e960418    # 0.29299998f

    .line 11
    .local v4, "startY":F
    const/high16 v0, 0x3f000000    # 0.5f

    .line 13
    .local v0, "center":F
    const/16 v5, 0xc

    new-array v5, v5, [F

    fill-array-data v5, :array_0

    iput-object v5, p0, Lat/markushi/ui/action/DrawerAction;->lineData:[F

    .line 20
    return-void

    .line 13
    nop

    :array_0
    .array-data 4
        0x3e0ccccd    # 0.1375f
        0x3e960418    # 0.29299998f
        0x3f5ccccd    # 0.8625f
        0x3e960418    # 0.29299998f
        0x3e0ccccd    # 0.1375f
        0x3f000000    # 0.5f
        0x3f5ccccd    # 0.8625f
        0x3f000000    # 0.5f
        0x3e0ccccd    # 0.1375f
        0x3f34fdf4    # 0.707f
        0x3f5ccccd    # 0.8625f
        0x3f34fdf4    # 0.707f
    .end array-data
.end method
