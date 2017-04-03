.class public final Lit/neokree/materialtabs/R$styleable;
.super Ljava/lang/Object;
.source "R.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lit/neokree/materialtabs/R;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "styleable"
.end annotation


# static fields
.field public static final ActionView:[I

.field public static final ActionView_av_action:I = 0x1

.field public static final ActionView_av_color:I = 0x0

.field public static final MaterialTabHost:[I

.field public static final MaterialTabHost_accentColor:I = 0x2

.field public static final MaterialTabHost_hasIcons:I = 0x0

.field public static final MaterialTabHost_iconColor:I = 0x3

.field public static final MaterialTabHost_materialTabsPrimaryColor:I = 0x1

.field public static final MaterialTabHost_textColor:I = 0x4


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 55
    const/4 v0, 0x2

    new-array v0, v0, [I

    fill-array-data v0, :array_0

    sput-object v0, Lit/neokree/materialtabs/R$styleable;->ActionView:[I

    .line 58
    const/4 v0, 0x5

    new-array v0, v0, [I

    fill-array-data v0, :array_1

    sput-object v0, Lit/neokree/materialtabs/R$styleable;->MaterialTabHost:[I

    .line 63
    return-void

    .line 55
    nop

    :array_0
    .array-data 4
        0x7f010005
        0x7f010006
    .end array-data

    .line 58
    :array_1
    .array-data 4
        0x7f010000
        0x7f010001
        0x7f010002
        0x7f010003
        0x7f010004
    .end array-data
.end method

.method public constructor <init>()V
    .locals 0

    .prologue
    .line 54
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method
