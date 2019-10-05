#version 330

out vec4 fragColor;

struct Fog
{
    int activeFog;
    vec3 color;
    float density;
};

uniform sampler2D positionsText;
uniform sampler2D depthText;
uniform sampler2D sceneText;

uniform vec2 screenSize;

uniform mat4 viewMatrix;
uniform Fog fog;
uniform vec3 ambientLight;
uniform vec3 lightColor;
uniform float lightIntensity;

vec2 getTextCoord()
{
    return gl_FragCoord.xy / screenSize;
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, vec3 lightColor, float lightIntensity)
{
    vec3 fogColor = fog.color * (ambientLight + lightColor * lightIntensity);
    float distance = length(pos);
    float fogFactor = 1.0 / exp( (distance * fog.density)* (distance * fog.density));
    fogFactor = clamp( fogFactor, 0.0, 1.0 );

    vec3 resultColor = mix(fogColor, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

void main()
{
    vec2 textCoord = getTextCoord();
    vec3 worldPos = texture(positionsText, textCoord).xyz;
    vec4 color = vec4(texture(sceneText, textCoord).xyz, 1);
    vec4 mvVertexPos = viewMatrix * vec4(worldPos, 1);
    float depth = texture(depthText, textCoord).r;
    if ( depth == 1 ) {
        discard;
    }
    if ( fog.activeFog == 1 )
    {
    	fragColor = calcFog(mvVertexPos.xyz, color, fog, ambientLight, lightColor, lightIntensity);
    }
    else {
	    fragColor = color;
    }
}