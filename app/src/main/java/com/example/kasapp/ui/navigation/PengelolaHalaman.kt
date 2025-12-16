package com.example.kasapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Import Destinasi
import com.example.kasapp.ui.navigation.AppHome
import com.example.kasapp.ui.navigation.HomeMenu
import com.example.kasapp.ui.navigation.InsertMenu
import com.example.kasapp.ui.navigation.UpdateMenu
import com.example.kasapp.ui.navigation.SuccessScreenMenu
import com.example.kasapp.ui.navigation.SuccessScreenKasir
import com.example.kasapp.ui.navigation.Kasir
import com.example.kasapp.ui.navigation.RincianPesanan
import com.example.kasapp.ui.navigation.NotaPesanan
import com.example.kasapp.ui.navigation.Riwayat
import com.example.kasapp.ui.navigation.DetailRiwayat
import com.example.kasapp.ui.navigation.Profile
import com.example.kasapp.ui.navigation.TentangKami // <--- IMPORT INI

// Import View
import com.example.kasapp.ui.view.HomeView
import com.example.kasapp.ui.view.HomeProfileView
import com.example.kasapp.ui.view.TentangKamiView // <--- PASTIKAN INI SUDAH DI-IMPORT (Sesuaikan package-nya)
import com.example.kasapp.ui.view.Riwayat.DetailRiwayatView
import com.example.kasapp.ui.view.Riwayat.RiwayatView
import com.example.kasapp.ui.view.kasir.HomeKasirView
import com.example.kasapp.ui.view.kasir.NotaPesananView
import com.example.kasapp.ui.view.kasir.RincianPesananView
import com.example.kasapp.ui.view.kasir.SuccessViewKasir
import com.example.kasapp.ui.view.menu.HomeMenuView
import com.example.kasapp.ui.view.menu.InsertMenuView
import com.example.kasapp.ui.view.menu.SuccessView
import com.example.kasapp.ui.view.menu.UpdateMenuView

import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PengelolaHalaman(
    navController: NavHostController = rememberNavController()
) {
    val kasirViewModel: KasirViewModel = viewModel(factory = ViewModelFactory.Factory)
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = AppHome.route,
    ) {
        // --- HALAMAN UTAMA ---
        composable(route = AppHome.route) {
            HomeView(
                onNavigateToKelolaMenu = { navController.navigate(HomeMenu.route) },
                onNavigateToKasir = { navController.navigate(Kasir.route) },
                onNavigateToRiwayat = { navController.navigate(Riwayat.route) },
                onNavigateToProfile = { navController.navigate(Profile.route) }
            )
        }

        // --- FITUR KELOLA MENU ---
        composable(route = HomeMenu.route) {
            HomeMenuView(
                onTambahMenuClick = { navController.navigate(InsertMenu.route) },
                onEditMenuClick = { idMenu -> navController.navigate("${UpdateMenu.route}/$idMenu") }
            )
        }

        composable(route = InsertMenu.route) {
            InsertMenuView(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.navigate(SuccessScreenMenu.route) }
            )
        }

        composable(
            route = UpdateMenu.routeWithArgs,
            arguments = listOf(navArgument(UpdateMenu.argIdMenu) { type = NavType.IntType })
        ) {
            UpdateMenuView(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.navigate(SuccessScreenMenu.route) }
            )
        }

        composable(route = SuccessScreenMenu.route) {
            SuccessView(
                onNavigateBack = {
                    navController.navigate(HomeMenu.route) {
                        popUpTo(HomeMenu.route) { inclusive = true }
                    }
                }
            )
        }

        // --- FITUR KASIR ---
        composable(route = Kasir.route) {
            HomeKasirView(
                viewModel = kasirViewModel,
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate(RincianPesanan.route) }
            )
        }

        composable(route = RincianPesanan.route) {
            RincianPesananView(
                viewModel = kasirViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.navigate(SuccessScreenKasir.route) }
            )
        }

        composable(route = SuccessScreenKasir.route) {
            SuccessViewKasir (
                onNavigateBack = {
                    navController.navigate(NotaPesanan.route) {
                        popUpTo(Kasir.route) { inclusive = false }
                    }
                }
            )
        }

        composable(route = NotaPesanan.route) {
            NotaPesananView(
                viewModel = kasirViewModel,
                onSelesaiClick = {
                    scope.launch { kasirViewModel.clearCart() }
                    navController.popBackStack(route = Kasir.route, inclusive = false)
                }
            )
        }

        // --- FITUR RIWAYAT ---
        composable(route = Riwayat.route) {
            RiwayatView(
                onBackClick = { navController.popBackStack() },
                onNotaClick = { idTransaksi ->
                    scope.launch {
                        kasirViewModel.loadCartFromHistory(idTransaksi)
                        withContext(Dispatchers.Main) {
                            navController.navigate(DetailRiwayat.route)
                        }
                    }
                }
            )
        }

        composable(route = DetailRiwayat.route) {
            DetailRiwayatView(
                viewModel = kasirViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- PROFILE ---
        composable(route = Profile.route) {
            HomeProfileView(
                onBackClick = { navController.popBackStack() },

                // 1. ISI NAVIGASI KE TENTANG KAMI DI SINI
                onTentangKamiClick = {
                    navController.navigate(TentangKami.route)
                },

                onKeluarAkunClick = { /* Implementasi logout */ }
            )
        }

        // --- HALAMAN TENTANG KAMI (BARU) ---
        composable(route = TentangKami.route) {
            TentangKamiView(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}