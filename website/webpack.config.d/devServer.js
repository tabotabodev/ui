// Serves index.html for every path in development, so deep links such as /principles/color/
// load the app (GitHub Pages gets real per-route copies from scripts/assemble-site.sh instead).
if (config.devServer) {
    config.devServer.historyApiFallback = true;
}
