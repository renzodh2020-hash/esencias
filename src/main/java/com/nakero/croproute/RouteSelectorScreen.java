package com.nakero.croproute;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.List;

public class RouteSelectorScreen extends Screen {
    private int page;

    public RouteSelectorScreen() {
        this(0);
    }

    private RouteSelectorScreen(int page) {
        super(Text.literal("Rutas guardadas"));
        this.page = Math.max(0, page);
    }

    @Override
    protected void init() {
        List<RouteData> routes = RouteManager.getRoutesNewestFirst();
        // Reducir las filas visibles en resoluciones pequenas, sin ocultar botones.
        int routesPerPage = height >= 390 ? 6 : height >= 330 ? 4 : height >= 280 ? 3 : 2;
        int maxPage = Math.max(0, (routes.size() - 1) / routesPerPage);
        page = Math.min(page, maxPage);

        int panelWidth = Math.min(330, width - 30);
        int x = (width - panelWidth) / 2;
        int startY = height < 280 ? 30 : Math.max(45,
                (height - (routesPerPage * 24 + 175)) / 2);

        int from = page * routesPerPage;
        int to = Math.min(routes.size(), from + routesPerPage);

        for (int i = from; i < to; i++) {
            RouteData route = routes.get(i);
            RouteData selected = RouteManager.getSelectedRoute();
            boolean isSelected = selected != null && selected.id.equals(route.id);

            String label = (isSelected ? "▶ " : "")
                    + route.name
                    + "  [" + route.points.size() + " pts]";

            int row = i - from;

            addDrawableChild(ButtonWidget.builder(
                    Text.literal(label),
                    button -> {
                        RouteManager.select(route.id);
                        CropRouteClient.showActionBar("Ruta seleccionada: " + route.name);
                        if (client != null) {
                            client.setScreen(null);
                        }
                    }
            ).dimensions(x, startY + row * 24, panelWidth, 20).build());
        }

        int bottomY = startY + routesPerPage * 24 + 7;

        if (page > 0) {
            addDrawableChild(ButtonWidget.builder(
                    Text.literal("< Anterior"),
                    button -> {
                        if (client != null) {
                            client.setScreen(new RouteSelectorScreen(page - 1));
                        }
                    }
            ).dimensions(x, bottomY, 90, 20).build());
        }

        if (page < maxPage) {
            addDrawableChild(ButtonWidget.builder(
                    Text.literal("Siguiente >"),
                    button -> {
                        if (client != null) {
                            client.setScreen(new RouteSelectorScreen(page + 1));
                        }
                    }
            ).dimensions(x + panelWidth - 90, bottomY, 90, 20).build());
        }

        RouteData selected = RouteManager.getSelectedRoute();
        boolean canDelete = selected != null;

        ButtonWidget deleteButton = ButtonWidget.builder(
                Text.literal("Eliminar seleccionada"),
                button -> {
                    RouteData current = RouteManager.getSelectedRoute();
                    if (current != null) {
                        String deletedName = current.name;
                        RouteManager.delete(current.id);
                        CropRouteClient.showActionBar("Ruta eliminada: " + deletedName);
                        if (client != null) {
                            client.setScreen(new RouteSelectorScreen(page));
                        }
                    }
                }
        ).dimensions(x + (panelWidth - 150) / 2, bottomY + 27, 150, 20).build();
        deleteButton.active = canDelete;
        addDrawableChild(deleteButton);

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Temporizador"),
                button -> {
                    if (client != null) {
                        client.setScreen(new TimerConfigScreen());
                    }
                }
        ).dimensions(x + (panelWidth - 180) / 2, bottomY + 54, 180, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Vueltas P"),
                button -> {
                    if (client != null) client.setScreen(new LapsConfigScreen());
                }
        ).dimensions(x + (panelWidth - 180) / 2, bottomY + 81, 180, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Pausas P"),
                button -> {
                    if (client != null) client.setScreen(new PausesConfigScreen());
                }
        ).dimensions(x + (panelWidth - 180) / 2, bottomY + 108, 180, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Cerrar"),
                button -> {
                    if (client != null) {
                        client.setScreen(null);
                    }
                }
        ).dimensions(x + (panelWidth - 90) / 2, bottomY + 135, 90, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("Crop Route — elige una ruta"),
                width / 2,
                22,
                0xFFFFFF
        );

        RouteData selected = RouteManager.getSelectedRoute();
        String selectedText = selected == null
                ? "Seleccionada: ninguna"
                : "Seleccionada: " + selected.name
                    + " | ~" + Math.round(selected.approximateLength()) + " bloques";

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal(selectedText),
                width / 2,
                37,
                0xCFCFCF
        );

        if (RouteManager.getRoutes().isEmpty()) {
            context.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.literal("Todavía no hay rutas. Presiona J en el mundo para grabar una."),
                    width / 2,
                    height / 2 - 5,
                    0xFFFFFF
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private static class TimerConfigScreen extends Screen {

        private TextFieldWidget hoursField;
        private TextFieldWidget minutesField;

        protected TimerConfigScreen() {
            super(Text.literal("Temporizador"));
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            renderBackground(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("Temporizador (solo tecla O)"), width / 2, 35, 0xFFFFFF);
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("Horas"), width / 2, 56, 0xCFCFCF);
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("Minutos"), width / 2, 91, 0xCFCFCF);
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        protected void init() {

            hoursField = new TextFieldWidget(
                    textRenderer,
                    width / 2 - 60,
                    70,
                    120,
                    20,
                    Text.literal("Horas")
            );

            minutesField = new TextFieldWidget(
                    textRenderer,
                    width / 2 - 60,
                    105,
                    120,
                    20,
                    Text.literal("Minutos")
            );

            hoursField.setText(
                    String.valueOf(RouteManager.getRouteTimerHours())
            );

            minutesField.setText(
                    String.valueOf(RouteManager.getRouteTimerMinutes())
            );

            addDrawableChild(hoursField);
            addDrawableChild(minutesField);

            addDrawableChild(
                    ButtonWidget.builder(
                            Text.literal("Guardar"),
                            button -> {

                                try {
                                    int hours = Integer.parseInt(hoursField.getText());
                                    int minutes = Integer.parseInt(minutesField.getText());

                                    RouteManager.setRouteTimer(hours, minutes);

                                    CropRouteClient.showChatMessage(
                                            "[Crop Route] Temporizador cambiado a "
                                                    + RouteManager.getRouteTimerHours() + " h "
                                                    + RouteManager.getRouteTimerMinutes() + " min. "
                                                    + "Se aplicara al iniciar el recorrido con O."
                                    );

                                } catch (Exception ignored) {
                                    CropRouteClient.showChatMessage(
                                            "[Crop Route] Tiempo invalido. Usa al menos 1 minuto "
                                                    + "(horas 0-999, minutos 0-59)."
                                    );
                                }

                                if (client != null) {
                                    client.setScreen(
                                            new RouteSelectorScreen()
                                    );
                                }
                            }
                    ).dimensions(
                            width / 2 - 60,
                            140,
                            120,
                            20
                    ).build()
            );

            addDrawableChild(
                    ButtonWidget.builder(
                            Text.literal("Volver"),
                            button -> {
                                if (client != null) {
                                    client.setScreen(
                                            new RouteSelectorScreen()
                                    );
                                }
                            }
                    ).dimensions(
                            width / 2 - 60,
                            170,
                            120,
                            20
                    ).build()
            );
        }
    }

    private static class LapsConfigScreen extends Screen {
        private static final String[] NAMES = {"RTAFK", "verruga11", "verruga1"};
        private final TextFieldWidget[] fields = new TextFieldWidget[3];

        protected LapsConfigScreen() { super(Text.literal("Vueltas P")); }

        @Override
        protected void init() {
            int top = Math.max(55, height / 2 - 77);
            for (int i = 0; i < 3; i++) {
                final int index = i;
                fields[i] = new TextFieldWidget(textRenderer, width / 2 + 12,
                        top + i * 34, 92, 20, Text.literal(NAMES[i]));
                fields[i].setText(String.valueOf(RouteManager.getSequenceLaps(index)));
                fields[i].setMaxLength(6);
                addDrawableChild(fields[i]);
            }
            addDrawableChild(ButtonWidget.builder(Text.literal("Guardar vueltas"), button -> {
                try {
                    int[] newLaps = new int[3];
                    for (int i = 0; i < 3; i++) newLaps[i] = Integer.parseInt(fields[i].getText().trim());
                    RouteManager.setSequenceLaps(newLaps);
                    CropRouteClient.showChatMessage("[Crop Route] Vueltas P guardadas: RTAFK "
                            + newLaps[0] + " | verruga11 " + newLaps[1]
                            + " | verruga1 " + newLaps[2] + ". Se aplicaran al reiniciar P.");
                    if (client != null) client.setScreen(new RouteSelectorScreen());
                } catch (Exception e) {
                    CropRouteClient.showChatMessage("[Crop Route] Vueltas invalidas. Usa enteros entre 1 y 100000.");
                }
            }).dimensions(width / 2 - 99, top + 115, 198, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Volver"), button -> {
                if (client != null) client.setScreen(new RouteSelectorScreen());
            }).dimensions(width / 2 - 50, top + 142, 100, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            renderBackground(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Vueltas P (1 a 100000)"),
                    width / 2, 27, 0xFFFFFF);
            int top = Math.max(55, height / 2 - 77);
            for (int i = 0; i < 3; i++) {
                context.drawTextWithShadow(textRenderer, Text.literal(NAMES[i]),
                        width / 2 - 105, top + i * 34 + 6, 0xD0D0D0);
            }
            super.render(context, mouseX, mouseY, delta);
        }
    }

    private static class PausesConfigScreen extends Screen {
        private static final String[] NAMES = {"RTAFK", "verruga11", "verruga1"};
        private final TextFieldWidget[] minFields = new TextFieldWidget[3];
        private final TextFieldWidget[] maxFields = new TextFieldWidget[3];
        private String error = "";

        protected PausesConfigScreen() { super(Text.literal("Pausas P")); }

        private static String asClock(int seconds) {
            return String.format(java.util.Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60);
        }

        // Valores en minutos:segundos. Se permiten de 00:00 a 9999:59.
        private static int parseClock(String value) {
            String[] parts = value.trim().split(":", -1);
            if (parts.length != 2 || !parts[0].matches("[0-9]{1,4}")
                    || !parts[1].matches("[0-9]{1,2}")) {
                throw new IllegalArgumentException("Usa MM:SS");
            }
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            if (seconds > 59) throw new IllegalArgumentException("Segundos: 0-59");
            return minutes * 60 + seconds;
        }

        @Override
        protected void init() {
            int top = Math.max(69, height / 2 - 70);
            for (int i = 0; i < 3; i++) {
                minFields[i] = new TextFieldWidget(textRenderer, width / 2 - 13,
                        top + i * 30, 69, 20, Text.literal("Minimo"));
                maxFields[i] = new TextFieldWidget(textRenderer, width / 2 + 66,
                        top + i * 30, 69, 20, Text.literal("Maximo"));
                minFields[i].setMaxLength(7);
                maxFields[i].setMaxLength(7);
                minFields[i].setText(asClock(RouteManager.getPauseMinimumSeconds(i)));
                maxFields[i].setText(asClock(RouteManager.getPauseMaximumSeconds(i)));
                addDrawableChild(minFields[i]);
                addDrawableChild(maxFields[i]);
            }
            addDrawableChild(ButtonWidget.builder(Text.literal("Guardar pausas"), button -> {
                try {
                    int[] minimums = new int[3];
                    int[] maximums = new int[3];
                    for (int i = 0; i < 3; i++) {
                        minimums[i] = parseClock(minFields[i].getText());
                        maximums[i] = parseClock(maxFields[i].getText());
                    }
                    RouteManager.setSequencePauses(minimums, maximums);
                    CropRouteClient.showChatMessage("[Crop Route] Pausas P guardadas: RTAFK "
                            + asClock(minimums[0]) + "-" + asClock(maximums[0])
                            + " | verruga11 " + asClock(minimums[1]) + "-" + asClock(maximums[1])
                            + " | verruga1 " + asClock(minimums[2]) + "-" + asClock(maximums[2])
                            + ". Se aplicaran al reiniciar P.");
                    if (client != null) client.setScreen(new RouteSelectorScreen());
                } catch (Exception e) {
                    error = "Formato MM:SS; minimo no puede superar maximo.";
                    CropRouteClient.showChatMessage("[Crop Route] " + error);
                }
            }).dimensions(width / 2 - 100, top + 101, 200, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Volver"), button -> {
                if (client != null) client.setScreen(new RouteSelectorScreen());
            }).dimensions(width / 2 - 50, top + 128, 100, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            renderBackground(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("Pausas P (minutos:segundos)"), width / 2, 23, 0xFFFFFF);
            int top = Math.max(69, height / 2 - 70);
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Minimo       Maximo"),
                    width / 2 + 48, top - 17, 0xB0B0B0);
            for (int i = 0; i < 3; i++) {
                context.drawTextWithShadow(textRenderer, Text.literal(NAMES[i]),
                        width / 2 - 122, top + i * 30 + 6, 0xD0D0D0);
            }
            if (!error.isEmpty()) context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal(error), width / 2, top + 160, 0xFF7777);
            super.render(context, mouseX, mouseY, delta);
        }
    }

}
