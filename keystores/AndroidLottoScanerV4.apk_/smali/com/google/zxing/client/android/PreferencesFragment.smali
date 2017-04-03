.class public final Lcom/google/zxing/client/android/PreferencesFragment;
.super Landroid/preference/PreferenceFragment;
.source "PreferencesFragment.java"

# interfaces
.implements Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/google/zxing/client/android/PreferencesFragment$CustomSearchURLValidator;
    }
.end annotation


# instance fields
.field private checkBoxPrefs:[Landroid/preference/CheckBoxPreference;


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 35
    invoke-direct {p0}, Landroid/preference/PreferenceFragment;-><init>()V

    return-void
.end method

.method private disableLastCheckedPref()V
    .locals 9

    .prologue
    const/4 v3, 0x1

    const/4 v4, 0x0

    .line 76
    new-instance v0, Ljava/util/ArrayList;

    iget-object v5, p0, Lcom/google/zxing/client/android/PreferencesFragment;->checkBoxPrefs:[Landroid/preference/CheckBoxPreference;

    array-length v5, v5

    invoke-direct {v0, v5}, Ljava/util/ArrayList;-><init>(I)V

    .line 77
    .local v0, "checked":Ljava/util/Collection;, "Ljava/util/Collection<Landroid/preference/CheckBoxPreference;>;"
    iget-object v6, p0, Lcom/google/zxing/client/android/PreferencesFragment;->checkBoxPrefs:[Landroid/preference/CheckBoxPreference;

    array-length v7, v6

    move v5, v4

    :goto_0
    if-lt v5, v7, :cond_0

    .line 82
    invoke-interface {v0}, Ljava/util/Collection;->size()I

    move-result v5

    if-gt v5, v3, :cond_2

    move v1, v3

    .line 83
    .local v1, "disable":Z
    :goto_1
    iget-object v7, p0, Lcom/google/zxing/client/android/PreferencesFragment;->checkBoxPrefs:[Landroid/preference/CheckBoxPreference;

    array-length v8, v7

    move v6, v4

    :goto_2
    if-lt v6, v8, :cond_3

    .line 86
    return-void

    .line 77
    .end local v1    # "disable":Z
    :cond_0
    aget-object v2, v6, v5

    .line 78
    .local v2, "pref":Landroid/preference/CheckBoxPreference;
    invoke-virtual {v2}, Landroid/preference/CheckBoxPreference;->isChecked()Z

    move-result v8

    if-eqz v8, :cond_1

    .line 79
    invoke-interface {v0, v2}, Ljava/util/Collection;->add(Ljava/lang/Object;)Z

    .line 77
    :cond_1
    add-int/lit8 v5, v5, 0x1

    goto :goto_0

    .end local v2    # "pref":Landroid/preference/CheckBoxPreference;
    :cond_2
    move v1, v4

    .line 82
    goto :goto_1

    .line 83
    .restart local v1    # "disable":Z
    :cond_3
    aget-object v2, v7, v6

    .line 84
    .restart local v2    # "pref":Landroid/preference/CheckBoxPreference;
    if-eqz v1, :cond_4

    invoke-interface {v0, v2}, Ljava/util/Collection;->contains(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_4

    move v5, v4

    :goto_3
    invoke-virtual {v2, v5}, Landroid/preference/CheckBoxPreference;->setEnabled(Z)V

    .line 83
    add-int/lit8 v5, v6, 0x1

    move v6, v5

    goto :goto_2

    :cond_4
    move v5, v3

    .line 84
    goto :goto_3
.end method

.method private static varargs findDecodePrefs(Landroid/preference/PreferenceScreen;[Ljava/lang/String;)[Landroid/preference/CheckBoxPreference;
    .locals 3
    .param p0, "preferences"    # Landroid/preference/PreferenceScreen;
    .param p1, "keys"    # [Ljava/lang/String;

    .prologue
    .line 63
    array-length v2, p1

    new-array v1, v2, [Landroid/preference/CheckBoxPreference;

    .line 64
    .local v1, "prefs":[Landroid/preference/CheckBoxPreference;
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_0
    array-length v2, p1

    if-lt v0, v2, :cond_0

    .line 67
    return-object v1

    .line 65
    :cond_0
    aget-object v2, p1, v0

    invoke-virtual {p0, v2}, Landroid/preference/PreferenceScreen;->findPreference(Ljava/lang/CharSequence;)Landroid/preference/Preference;

    move-result-object v2

    check-cast v2, Landroid/preference/CheckBoxPreference;

    aput-object v2, v1, v0

    .line 64
    add-int/lit8 v0, v0, 0x1

    goto :goto_0
.end method


# virtual methods
.method public onCreate(Landroid/os/Bundle;)V
    .locals 5
    .param p1, "icicle"    # Landroid/os/Bundle;

    .prologue
    .line 43
    invoke-super {p0, p1}, Landroid/preference/PreferenceFragment;->onCreate(Landroid/os/Bundle;)V

    .line 44
    const/high16 v2, 0x7f050000

    invoke-virtual {p0, v2}, Lcom/google/zxing/client/android/PreferencesFragment;->addPreferencesFromResource(I)V

    .line 46
    invoke-virtual {p0}, Lcom/google/zxing/client/android/PreferencesFragment;->getPreferenceScreen()Landroid/preference/PreferenceScreen;

    move-result-object v1

    .line 47
    .local v1, "preferences":Landroid/preference/PreferenceScreen;
    invoke-virtual {v1}, Landroid/preference/PreferenceScreen;->getSharedPreferences()Landroid/content/SharedPreferences;

    move-result-object v2

    invoke-interface {v2, p0}, Landroid/content/SharedPreferences;->registerOnSharedPreferenceChangeListener(Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;)V

    .line 48
    const/4 v2, 0x6

    new-array v2, v2, [Ljava/lang/String;

    const/4 v3, 0x0

    .line 49
    const-string v4, "preferences_decode_1D_product"

    aput-object v4, v2, v3

    const/4 v3, 0x1

    .line 50
    const-string v4, "preferences_decode_1D_industrial"

    aput-object v4, v2, v3

    const/4 v3, 0x2

    .line 51
    const-string v4, "preferences_decode_QR"

    aput-object v4, v2, v3

    const/4 v3, 0x3

    .line 52
    const-string v4, "preferences_decode_Data_Matrix"

    aput-object v4, v2, v3

    const/4 v3, 0x4

    .line 53
    const-string v4, "preferences_decode_Aztec"

    aput-object v4, v2, v3

    const/4 v3, 0x5

    .line 54
    const-string v4, "preferences_decode_PDF417"

    aput-object v4, v2, v3

    .line 48
    invoke-static {v1, v2}, Lcom/google/zxing/client/android/PreferencesFragment;->findDecodePrefs(Landroid/preference/PreferenceScreen;[Ljava/lang/String;)[Landroid/preference/CheckBoxPreference;

    move-result-object v2

    iput-object v2, p0, Lcom/google/zxing/client/android/PreferencesFragment;->checkBoxPrefs:[Landroid/preference/CheckBoxPreference;

    .line 55
    invoke-direct {p0}, Lcom/google/zxing/client/android/PreferencesFragment;->disableLastCheckedPref()V

    .line 58
    const-string v2, "preferences_custom_product_search"

    invoke-virtual {v1, v2}, Landroid/preference/PreferenceScreen;->findPreference(Ljava/lang/CharSequence;)Landroid/preference/Preference;

    move-result-object v0

    .line 57
    check-cast v0, Landroid/preference/EditTextPreference;

    .line 59
    .local v0, "customProductSearch":Landroid/preference/EditTextPreference;
    new-instance v2, Lcom/google/zxing/client/android/PreferencesFragment$CustomSearchURLValidator;

    const/4 v3, 0x0

    invoke-direct {v2, p0, v3}, Lcom/google/zxing/client/android/PreferencesFragment$CustomSearchURLValidator;-><init>(Lcom/google/zxing/client/android/PreferencesFragment;Lcom/google/zxing/client/android/PreferencesFragment$CustomSearchURLValidator;)V

    invoke-virtual {v0, v2}, Landroid/preference/EditTextPreference;->setOnPreferenceChangeListener(Landroid/preference/Preference$OnPreferenceChangeListener;)V

    .line 60
    return-void
.end method

.method public onSharedPreferenceChanged(Landroid/content/SharedPreferences;Ljava/lang/String;)V
    .locals 0
    .param p1, "sharedPreferences"    # Landroid/content/SharedPreferences;
    .param p2, "key"    # Ljava/lang/String;

    .prologue
    .line 72
    invoke-direct {p0}, Lcom/google/zxing/client/android/PreferencesFragment;->disableLastCheckedPref()V

    .line 73
    return-void
.end method
