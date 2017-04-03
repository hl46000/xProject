.class public Lat/markushi/ui/action/BackAction;
.super Lat/markushi/ui/action/Action;
.source "BackAction.java"


# direct methods
.method public constructor <init>()V
    .locals 8

    .prologue
    .line 5
    invoke-direct {p0}, Lat/markushi/ui/action/Action;-><init>()V

    .line 7
    const v1, 0x3f51eb85    # 0.82f

    .line 8
    .local v1, "endX":F
    const/high16 v2, 0x3e600000    # 0.21875f

    .line 9
    .local v2, "startX":F
    const/high16 v0, 0x3f000000    # 0.5f

    .line 11
    .local v0, "center":F
    const/16 v3, 0xc

    new-array v3, v3, [F

    fill-array-data v3, :array_0

    iput-object v3, p0, Lat/markushi/ui/action/BackAction;->lineData:[F

    .line 19
    iget-object v3, p0, Lat/markushi/ui/action/BackAction;->lineSegments:Ljava/util/List;

    new-instance v4, Lat/markushi/ui/action/LineSegment;

    const/4 v5, 0x2

    new-array v5, v5, [I

    fill-array-data v5, :array_1

    invoke-direct {v4, v5}, Lat/markushi/ui/action/LineSegment;-><init>([I)V

    invoke-interface {v3, v4}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 20
    iget-object v3, p0, Lat/markushi/ui/action/BackAction;->lineSegments:Ljava/util/List;

    new-instance v4, Lat/markushi/ui/action/LineSegment;

    const/4 v5, 0x1

    new-array v5, v5, [I

    const/4 v6, 0x0

    const/4 v7, 0x4

    aput v7, v5, v6

    invoke-direct {v4, v5}, Lat/markushi/ui/action/LineSegment;-><init>([I)V

    invoke-interface {v3, v4}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 21
    return-void

    .line 11
    nop

    :array_0
    .array-data 4
        0x3e600000    # 0.21875f
        0x3f000000    # 0.5f
        0x3f051eb8    # 0.52f
        0x3e4ccccd    # 0.2f
        0x3e600000    # 0.21875f
        0x3f000000    # 0.5f
        0x3f51eb85    # 0.82f
        0x3f000000    # 0.5f
        0x3e600000    # 0.21875f
        0x3f000000    # 0.5f
        0x3f051eb8    # 0.52f
        0x3f4ccccd    # 0.8f
    .end array-data

    .line 19
    :array_1
    .array-data 4
        0x0
        0x8
    .end array-data
.end method
