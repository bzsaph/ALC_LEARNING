import { ReactInstance, Surface } from 'react-360-web';

function kati(bundle, parent, options = {}) {

  const SIZE = 300;

  const r360 = new ReactInstance(bundle, parent, {
    fullScreen: true,
    ...options,
  });

  // SPHERE
  const sphereSurface = new Surface(
    SIZE,
    SIZE,
    Surface.SurfaceShape.Flat,
  );
  sphereSurface.setAngle(0, 0);
  r360.renderToSurface(
    r360.createRoot('Info', { name: 'welcome to our course so in our course it will help you to start react 360 in command line'+'step1:npm install -g react-360-cli'+'step2:react-360 init Hello360'+'step3:react-360 init Hello360'+'step4:Enter the directory '+'step5:npm start'  }),
    sphereSurface,
  );

  const placedispray = new Surface(
    SIZE,
    SIZE,
    Surface.SurfaceShape.Flat,
  );
  placedispray.setAngle(0, 0);
  r360.renderToSurface(
    r360.createRoot('Info', { name: 'welcome to our course so in our course it will help you to start react 360 in command line'+'step1:npm install -g react-360-cli'+'step2:react-360 init Hello360'+'step3:react-360 init Hello360'+'step4:Enter the directory '+'step5:npm start'  }),
    placedispray,
  );
  // CAPSULE
  const capsuleSurface = new Surface(
    SIZE,
    SIZE,
    Surface.SurfaceShape.Flat,
  );
  capsuleSurface.setAngle(Math.PI / 2, 0);
  r360.renderToSurface(
    r360.createRoot('Info', { name: 'try this if it is working  index.js'+'client.js'+'index.html'+''+''+''+'' }),
    capsuleSurface,
  );

  // CYLINDER
  const cylinderSurface = new Surface(
    SIZE,
    SIZE,
    Surface.SurfaceShape.Flat,
  );
  cylinderSurface.setAngle(Math.PI, 0);
  r360.renderToSurface(
    r360.createRoot('Info', { name: 'Cylinder' }),
    cylinderSurface,
  );

  // CUBE
  const cubeSurface = new Surface(
    SIZE,
    SIZE,
    Surface.SurfaceShape.Flat,
  );
  cubeSurface.setAngle(-1 * Math.PI / 2, 0);
  r360.renderToSurface(
    r360.createRoot('Info', { name: 'Cube' }),
    cubeSurface,
  );

  // 3D SPHERE
  r360.renderToLocation(
    r360.createRoot('Sphere'),
    r360.getDefaultLocation(),
  );

  r360.compositor.setBackground(r360.getAssetURL('3d_scene.jpg'));
}

window.React360 = {kati};
