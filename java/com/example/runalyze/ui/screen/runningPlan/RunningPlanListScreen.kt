package com.example.runalyze.ui.screen.runningPlan

import androidx.compose.foundation.layout.Column // Importation du composant Column pour organiser les éléments verticalement
import androidx.compose.foundation.layout.fillMaxSize // Importation de la propriété fillMaxSize pour remplir entièrement l'espace disponible
import androidx.compose.foundation.layout.padding // Importation de la propriété padding pour ajouter des marges à un composant
import androidx.compose.material3.ExperimentalMaterial3Api // Importation de l'annotation ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme // Importation du thème Material
import androidx.compose.material3.Scaffold // Importation du composant Scaffold pour fournir une mise en page de base
import androidx.compose.material3.Surface // Importation du composant Surface pour fournir un fond à l'écran
import androidx.compose.material3.TopAppBarDefaults // Importation des paramètres par défaut de la barre supérieure
import androidx.compose.runtime.Composable // Importation de l'annotation Composable pour définir une fonction composable
import androidx.compose.ui.Alignment // Importation de l'enum Alignment pour aligner les éléments
import androidx.compose.ui.Modifier // Importation du type Modifier pour apporter des modifications aux composants
import androidx.compose.ui.input.nestedscroll.nestedScroll // Importation de la propriété nestedScroll pour prendre en charge le défilement imbriqué
import androidx.compose.ui.res.stringResource // Importation de la méthode stringResource pour accéder aux ressources de chaînes
import androidx.navigation.NavController // Importation du contrôleur de navigation NavController
import com.example.runalyze.R // Importation des ressources d'application
import com.example.runalyze.service.RunningPlan // Importation de la classe RunningPlan
import com.example.runalyze.ui.componentLibrary.ScreenHeader // Importation du composant ScreenHeader

@OptIn(ExperimentalMaterial3Api::class) // Annotation pour indiquer l'utilisation de fonctionnalités expérimentales
@Composable
// Vue pour l'écran des plans de course
fun RunningPlanListScreen(runningPlanList: List<RunningPlan>, navController: NavController) {
    Surface( // Composant Surface pour fournir un fond à l'écran
        modifier = Modifier.fillMaxSize(), // Modification pour remplir entièrement l'écran
        color = MaterialTheme.colorScheme.background // Couleur de fond définie par le thème Material
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior() // Récupération du comportement de défilement épinglé par défaut
        Scaffold( // Composant Scaffold pour fournir une mise en page de base
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection), // Modification pour prendre en charge le défilement imbriqué
            topBar = { // Barre supérieure du Scaffold
                ScreenHeader(stringResource(id = R.string.running_plans_header)) // Composant ScreenHeader pour l'en-tête de l'écran
            }) { values -> // Fonction lambda avec les valeurs de mise en page
            Column( // Composant Column pour organiser les éléments verticalement
                modifier = Modifier
                    .fillMaxSize() // Modification pour remplir entièrement l'écran
                    .padding(values), // Ajout de marges basées sur les valeurs de mise en page
                horizontalAlignment = Alignment.CenterHorizontally, // Alignement horizontal des éléments au centre
            ) {
                RunningPlanList(runningPlanList = runningPlanList, navController = navController) // Appel du composant RunningPlanList pour afficher la liste des plans de course
            }
        }
    }
}
