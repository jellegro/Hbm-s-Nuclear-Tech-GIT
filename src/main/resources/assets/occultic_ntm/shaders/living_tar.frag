#version 120

uniform sampler2D lightmap;
uniform float time;
uniform float opacity;
uniform bool fogEnabled;
uniform int fogMode;
varying vec3 eyePosition;
varying vec3 eyeNormal;
varying vec3 surfacePosition;
varying vec2 lightUV;
varying mat3 normalToEye;

float hash(vec3 p) {
    return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453);
}

float noise(vec3 p) {
    vec3 i = floor(p), f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(hash(i), hash(i + vec3(1,0,0)), f.x),
                   mix(hash(i + vec3(0,1,0)), hash(i + vec3(1,1,0)), f.x), f.y),
               mix(mix(hash(i + vec3(0,0,1)), hash(i + vec3(1,0,1)), f.x),
                   mix(hash(i + vec3(0,1,1)), hash(i + vec3(1,1,1)), f.x), f.y), f.z);
}

void main() {
    // Irregular viscous microrelief; no bands, UV scrolling or vertex waves.
    vec3 p = surfacePosition * vec3(13.0, 5.0, 13.0) - vec3(0.0, time * 0.075, 0.0);
    float film = noise(p);
    vec3 gradient = vec3(noise(p + vec3(0.12,0,0)) - film,
                         noise(p + vec3(0,0.12,0)) - film,
                         noise(p + vec3(0,0,0.12)) - film);
    vec3 n0 = normalize(eyeNormal);
    gradient = normalToEye * gradient;
    // Tangential relief does not bend the cuboid silhouette.
    vec3 n = normalize(n0 + (gradient - n0 * dot(gradient, n0)) * 1.8);
    vec3 v = normalize(-eyePosition);
    vec3 key = normalize(vec3(-0.45, 0.8, 0.65));
    vec3 fill = normalize(vec3(0.75, 0.3, 0.5));
    float facing = max(dot(n, v), 0.0);
    float fresnel = 0.045 + 0.955 * pow(1.0 - facing, 5.0);
    float specular = pow(max(dot(n, normalize(key + v)), 0.0), 80.0);
    float softReflection = pow(max(dot(reflect(-v, n), key), 0.0), 9.0);
    float edgeReflection = pow(max(dot(reflect(-v, n), fill), 0.0), 20.0);
    float broadFill = pow(max(dot(n, normalize(fill + v)), 0.0), 16.0);
    float broadKey = pow(max(dot(n, normalize(key + v)), 0.0), 12.0);
    float skyReflection = smoothstep(-0.1, 0.8, reflect(-v, n).y);
    vec3 light = texture2D(lightmap, lightUV).rgb;
    float ambient = max(light.r, max(light.g, light.b));
    vec3 resin = mix(vec3(0.012, 0.010, 0.009), vec3(0.030, 0.027, 0.024), film);
    float diffuse = 0.6 + 0.4 * max(dot(n, key), 0.0);
    vec3 gloss = vec3(0.82, 0.85, 0.87) * (specular * 0.65 + softReflection * 0.11
               + broadKey * 0.12 + broadFill * 0.075 + skyReflection * 0.035
               + fresnel * (0.035 + edgeReflection * 0.28));
    vec3 result = resin * light * diffuse + gloss * ambient;
    if (fogEnabled) {
        float distance = length(eyePosition);
        float fog;
        if (fogMode == 9729) fog = (gl_Fog.end - distance) * gl_Fog.scale;
        else if (fogMode == 2048) fog = exp(-gl_Fog.density * distance);
        else fog = exp(-gl_Fog.density * gl_Fog.density * distance * distance);
        result = mix(gl_Fog.color.rgb, result, clamp(fog, 0.0, 1.0));
    }
    gl_FragColor = vec4(result, opacity);
}
