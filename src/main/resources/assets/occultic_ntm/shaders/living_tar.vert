#version 120

uniform vec3 surfaceOrigin;
varying vec3 eyePosition;
varying vec3 eyeNormal;
varying vec3 surfacePosition;
varying vec2 lightUV;
varying mat3 normalToEye;

void main() {
    vec4 eye = gl_ModelViewMatrix * gl_Vertex;
    eyePosition = eye.xyz;
    eyeNormal = normalize(gl_NormalMatrix * gl_Normal);
    normalToEye = gl_NormalMatrix;
    surfacePosition = gl_Vertex.xyz + surfaceOrigin;
    lightUV = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
    gl_Position = gl_ProjectionMatrix * eye;
    gl_FrontColor = gl_Color;
}
